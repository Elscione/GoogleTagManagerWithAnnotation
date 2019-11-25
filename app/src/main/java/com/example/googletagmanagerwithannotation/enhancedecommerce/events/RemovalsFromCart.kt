package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.firebaseanalyticrules.rules.RemovalsFromCartRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(true, FirebaseAnalytics.Event.REMOVE_FROM_CART, RemovalsFromCartRules::class)
data class RemovalsFromCart(
    val items: ArrayList<Product>
)