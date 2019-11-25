package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.firebaseanalyticrules.rules.ProductDetailViewsRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(true, FirebaseAnalytics.Event.VIEW_ITEM, ProductDetailViewsRules::class)
data class ProductDetailViews(
    val items: List<Product>
)