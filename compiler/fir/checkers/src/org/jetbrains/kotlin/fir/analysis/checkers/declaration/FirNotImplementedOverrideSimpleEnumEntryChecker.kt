/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.checkers.declaration

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.unsubstitutedScope
import org.jetbrains.kotlin.fir.analysis.diagnostics.FirErrors.ABSTRACT_MEMBER_NOT_IMPLEMENTED_BY_ENUM_ENTRY
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.collectEnumEntries
import org.jetbrains.kotlin.fir.declarations.utils.isAbstract
import org.jetbrains.kotlin.fir.declarations.utils.isEnumClass
import org.jetbrains.kotlin.fir.declarations.utils.isExpect
import org.jetbrains.kotlin.fir.scopes.processAllCallables
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol

sealed class FirNotImplementedOverrideSimpleEnumEntryChecker(mppKind: MppCheckerKind) : FirClassChecker(mppKind) {
    object Regular : FirNotImplementedOverrideSimpleEnumEntryChecker(MppCheckerKind.Platform) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirClass) {
            if (declaration.isExpect) return
            super.check(declaration)
        }
    }

    object ForExpectClass : FirNotImplementedOverrideSimpleEnumEntryChecker(MppCheckerKind.Common) {
        context(context: CheckerContext, reporter: DiagnosticReporter)
        override fun check(declaration: FirClass) {
            if (!declaration.isExpect) return
            super.check(declaration)
        }
    }

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        if (!declaration.isEnumClass) return

        // Enum entries with an initializer are handled by FirNotImplementedOverrideChecker since they contain an AnonymousObject.
        val enumEntries = declaration.collectEnumEntries(context.session).filter { it.initializer == null && it.source != null }
        if (enumEntries.isEmpty()) return

        val enumScope = declaration.unsubstitutedScope()

        val notImplemented = mutableListOf<FirCallableSymbol<*>>()
        enumScope.processAllCallables { symbol ->
            if (symbol.isAbstract) {
                notImplemented.add(symbol)
            }
        }

        if (notImplemented.isEmpty()) return

        for (enumEntry in enumEntries) {
            reporter.reportOn(enumEntry.source, ABSTRACT_MEMBER_NOT_IMPLEMENTED_BY_ENUM_ENTRY, enumEntry.symbol, notImplemented)
        }
    }
}
