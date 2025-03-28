// WITH_STDLIB

@file:JvmName("TestKt")
package test

import kotlinx.parcelize.*
import android.os.Parcel
import android.os.Parcelable

data class User(val name: String, val age: Int)

@Parcelize
data class UserParcelable(val user: User) : Parcelable {
    private companion object : Parceler<UserParcelable> {
        override fun UserParcelable.write(parcel: Parcel, flags: Int) {
            parcel.writeString(user.name)
        }

        override fun create(parcel: Parcel) = UserParcelable(User(parcel.readString(), 0))
    }
}

fun box() = parcelTest { parcel ->
    val userParcelable = UserParcelable(User("John", 20))
    userParcelable.writeToParcel(parcel, 0)

    val bytes = parcel.marshall()
    parcel.unmarshall(bytes, 0, bytes.size)
    parcel.setDataPosition(0)

    val userParcelable2 = parcelableCreator<UserParcelable>().createFromParcel(parcel)

    assert(userParcelable.user.name == userParcelable2.user.name)
    assert(userParcelable2.user.age == 0)
}
