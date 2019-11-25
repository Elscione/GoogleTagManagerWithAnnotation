package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.firebaseanalyticrules.rules.AdditionsToCartRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(true, FirebaseAnalytics.Event.ADD_TO_CART, AdditionsToCartRules::class)
class AdditionsToCart(
    val items: List<Product>
)