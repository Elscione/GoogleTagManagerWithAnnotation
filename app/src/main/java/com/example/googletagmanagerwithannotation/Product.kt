package com.example.googletagmanagerwithannotation

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
    val nullableChar: Char?
)