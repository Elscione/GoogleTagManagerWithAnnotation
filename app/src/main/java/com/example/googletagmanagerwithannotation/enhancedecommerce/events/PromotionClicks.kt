package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.annotation.Key
import com.example.firebaseanalyticrules.rules.PromotionClicksRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Promotion
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(false, FirebaseAnalytics.Event.VIEW_ITEM, PromotionClicksRules::class)
data class PromotionClicks(
    @Key("promotions")
    val promotions: List<Promotion>,
    @Key(FirebaseAnalytics.Param.CONTENT_TYPE)
    val contentType: String,
    @Key(FirebaseAnalytics.Param.ITEM_ID)
    val itemId: String
)