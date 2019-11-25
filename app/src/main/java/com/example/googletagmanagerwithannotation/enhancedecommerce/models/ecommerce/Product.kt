package com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce

import com.example.annotation.BundleThis
import com.example.annotation.Key
import com.google.firebase.analytics.FirebaseAnalytics

@BundleThis(false, true)
data class Product(
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
    val currency: String
)