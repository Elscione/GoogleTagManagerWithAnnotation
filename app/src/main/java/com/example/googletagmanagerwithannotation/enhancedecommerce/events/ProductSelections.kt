package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.firebaseanalyticrules.rules.ProductSelectionsRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(true, FirebaseAnalytics.Event.VIEW_ITEM, ProductSelectionsRules::class)
data class ProductSelections(
    val item_list: String,
    val items: ArrayList<Product>
)