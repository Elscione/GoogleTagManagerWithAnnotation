package com.example.googletagmanagerwithannotation

import com.example.annotation.AnalyticEvent
import com.example.firebaseanalyticrules.ProductTestRules
import com.example.firebaseanalyticrules.rules.ProductClicksRules
import com.example.firebaseanalyticrules.rules.ProductImpressionsRules
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product
import com.google.firebase.analytics.FirebaseAnalytics

class Event {

    @AnalyticEvent(
        nameAsKey = true,
        eventKey = FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS,
        rulesClass = ProductImpressionsRules::class
    )
    class ProductImpression(
        val item_list: String,
        val items: ArrayList<Product>
    )

    @AnalyticEvent(
        nameAsKey = true,
        eventKey = FirebaseAnalytics.Event.SELECT_CONTENT,
        rulesClass = ProductClicksRules::class
    )
    class ProductClicks(
        val item_list: String,
        val items: ArrayList<Product>
    )

    @AnalyticEvent(
        nameAsKey = true,
        eventKey = FirebaseAnalytics.Event.SELECT_CONTENT,
        rulesClass = ProductTestRules::class
    )
    class ProductTest(
        val item_list: String,
        val items: ArrayList<Product>
    )
}