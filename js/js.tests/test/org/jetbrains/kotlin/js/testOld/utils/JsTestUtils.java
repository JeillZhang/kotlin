/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.testOld.utils;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.js.JavaScript;
import org.jetbrains.kotlin.js.config.EcmaVersion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

public final class JsTestUtils {

    private JsTestUtils() {
    }

    @NotNull
    public static EnumSet<EcmaVersion> successOnEcmaV5() {
        return EnumSet.of(EcmaVersion.es5);
    }

    @NotNull
    public static String convertFileNameToDotJsFile(@NotNull String filename, @NotNull EcmaVersion ecmaVersion) {
        String postFix = "_" + ecmaVersion.toString() + JavaScript.DOT_EXTENSION;
        int index = filename.lastIndexOf('.');
        if (index < 0) {
            return filename + postFix;
        }
        return filename.substring(0, index) + postFix;
    }

    @NotNull
    public static String readFile(@NotNull String path) throws IOException {
        return FileUtil.loadFile(new File(path), /*convertLineSeparators = */ true);
    }

    @NotNull
    public static List<String> getAllFilesInDir(@NotNull String dirName) {
        File dir = new File(dirName);
        assert dir.isDirectory() : dir + " is not a directory.";
        List<String> fullFilePaths = new ArrayList<>();
        for (String fileName : dir.list()) {
            fullFilePaths.add(dirName + "/" + fileName);
        }
        return fullFilePaths;
    }

    @NotNull
    public static List<String> getFilesInDirectoryByExtension(@NotNull String directory, String extension) {
        File dir = new File(directory);

        if (!dir.isDirectory()) return ContainerUtil.emptyList();

        List<File> files = FileUtil.findFilesByMask(Pattern.compile(".*\\." + extension + "$"), dir);

        return ContainerUtil.map(files, File::getAbsolutePath);
    }
}
