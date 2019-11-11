package com.example.googletagmanagerwithannotation

import android.os.Parcel
import android.os.Parcelable
import com.example.annotation.BundleThis
import com.example.annotation.defaultvalue.DefaultValueString

@BundleThis(nameAsKey = true, defaultAll = true)
data class Product(
    val nonNullableByte: Byte,
    val nullableByte: Byte?,
    val nonNullableShort: Short,
    val nullableShort: Short?,
    val nonNullableInt: Int,
    val nullableInt: Int?,
    val nonNullableDouble: Double,
    val nullableDouble: Double?,
    val nonNullableLong: Long,
    val nullableLong: Long?,
    val nonNullableBoolean: Boolean,
    val nullableBoolean: Boolean?,
    val nonNullableString: String,
    @DefaultValueString("Nullable default value")
    val nullableString: String?,
    val nonNullableChar: Char,
    val nullableChar: Char?,
    val item: Item
)

@BundleThis(nameAsKey = true, defaultAll = true)
data class Item(val title: String?): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}