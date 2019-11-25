package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.annotation.defaultvalues.DefaultValueString
import com.example.firebaseanalyticrules.rules.ProductClicksRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(true, FirebaseAnalytics.Event.SELECT_CONTENT, ProductClicksRules::class)
data class ProductClicks(
    @DefaultValueString("Search Results")
    val item_list: String,
    val items: ArrayList<Product>
)