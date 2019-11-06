package com.example.googletagmanagerwithannotation

import com.example.annotation.BundleKey
import com.example.annotation.BundleThis

@BundleThis(nameAsKey = false, defaultAll = false)
data class Product(
    @BundleKey("NON_NULLABLE_BYTE")
    val nonNullableByte: Byte,
    @BundleKey("NULLABLE_BYTE")
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
    val nullableString: String?,
    val nonNullableChar: Char,
    val nullableChar: Char?
)