package com.example.googletagmanagerwithannotation

import android.os.Parcel
import android.os.Parcelable
import com.example.annotation.BundleThis
import com.example.annotation.Key
import com.example.annotation.ObjectType
import com.example.annotation.defaultvalue.DefaultValueString
import com.google.firebase.analytics.FirebaseAnalytics

//@BundleThis(nameAsKey = true, defaultAll = true)
//data class Product(
//    val item: Item,
//    val nonNullableByte: Byte,
//    val nullableByte: Byte?,
//    val nonNullableShort: Short,
//    val nullableShort: Short?,
//    val nonNullableInt: Int,
//    val nullableInt: Int?,
//    val nonNullableDouble: Double,
//    val nullableDouble: Double?,
//    val nonNullableFloat: Float,
//    val nullableFloat: Float?,
//    val nonNullableLong: Long,
//    val nullableLong: Long?,
//    val nonNullableBoolean: Boolean,
//    val nullableBoolean: Boolean?,
//    val nonNullableString: String,
//    @DefaultValueString("Nullable default value")
//    val nullableString: String?,
//    val nonNullableChar: Char,
//    val nullableChar: Char?
//)

@BundleThis(nameAsKey = false, defaultAll = true)
data class Product (
    @Key(FirebaseAnalytics.Param.ITEM_ID)
    val id: String,
    @Key(FirebaseAnalytics.Param.ITEM_NAME)
    val name: String,
    @Key(FirebaseAnalytics.Param.ITEM_CATEGORY)
    val category: String,
    @Key(FirebaseAnalytics.Param.ITEM_VARIANT)
    val variant: String,
    @Key(FirebaseAnalytics.Param.ITEM_BRAND)
    val brand: String,
    @Key(FirebaseAnalytics.Param.PRICE)
    val price: Double,
    @Key(FirebaseAnalytics.Param.CURRENCY)
    val currency: String,
    @Key("Items")
    val shop: Shop
)