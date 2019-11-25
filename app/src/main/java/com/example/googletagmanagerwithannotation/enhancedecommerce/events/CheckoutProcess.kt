package com.example.googletagmanagerwithannotation.enhancedecommerce.events

import com.example.annotation.AnalyticEvent
import com.example.annotation.Key
import com.example.annotation.defaultvalues.DefaultValueInt
import com.example.annotation.defaultvalues.DefaultValueString
import com.example.firebaseanalyticrules.rules.CheckoutProcessRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

@AnalyticEvent(false, FirebaseAnalytics.Event.BEGIN_CHECKOUT, CheckoutProcessRules::class)
data class CheckoutProcess(
    @Key("items")
    val items: List<Product>,
    @Key(FirebaseAnalytics.Param.CHECKOUT_STEP)
    @DefaultValueInt(1)
    val checkoutStep: Int,
    @Key(FirebaseAnalytics.Param.CHECKOUT_OPTION)
    @DefaultValueString("Visa")
    val checkoutOption: String
)