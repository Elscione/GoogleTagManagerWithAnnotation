package com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce

import com.example.annotation.BundleThis
import com.example.annotation.Key
import com.google.firebase.analytics.FirebaseAnalytics

@BundleThis(false, true)
data class Promotion(
    @Key(FirebaseAnalytics.Param.ITEM_ID)
    val id: String,
    @Key(FirebaseAnalytics.Param.ITEM_NAME)
    val name: String,
    @Key(FirebaseAnalytics.Param.CREATIVE_NAME)
    val creativeName: String,
    @Key(FirebaseAnalytics.Param.CREATIVE_SLOT)
    val creativeSlot: String
)