// FILE: annotations.kt
package d.e.f

annotation class a

annotation class b

// FILE: topLevelMembersAnnotated.kt
package a.b.c.topLevelMembersAnnotated

import d.e.f.*

@property:a @property:b val i: Int = 0

@a @b fun f(@a @b p1: C): Int = 0


class C