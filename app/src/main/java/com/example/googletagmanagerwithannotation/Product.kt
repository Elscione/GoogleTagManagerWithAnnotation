package com.example.googletagmanagerwithannotation

import android.os.Parcel
import android.os.Parcelable
import com.example.annotation.BundleThis
import com.example.annotation.Key
import com.example.annotation.defaultvalue.DefaultValueString

@BundleThis(nameAsKey = true, defaultAll = true)
data class Product(
    val item: Item,
    val nonNullableByte: Byte,
    val nullableByte: Byte?,
    val nonNullableShort: Short,
    val nullableShort: Short?,
    val nonNullableInt: Int,
    val nullableInt: Int?,
    val nonNullableDouble: Double,
    val nullableDouble: Double?,
    val nonNullableFloat: Float,
    val nullableFloat: Float?,
    val nonNullableLong: Long,
    val nullableLong: Long?,
    val nonNullableBoolean: Boolean,
    val nullableBoolean: Boolean?,
    val nonNullableString: String,
    @DefaultValueString("Nullable default value")
    val nullableString: String?,
    val nonNullableChar: Char,
    val nullableChar: Char?
)

@BundleThis(nameAsKey = true, defaultAll = true)
data class Item(
    val title: String?,
    val price: Int,
    val quantity: Long?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(price)
        parcel.writeValue(quantity)
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