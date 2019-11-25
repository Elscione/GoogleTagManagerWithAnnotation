package com.example.googletagmanagerwithannotation

//import com.example.processor.AnalyticParameter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.ProductBundler
import com.example.googletagmanagerwithannotation.models.Badge
import com.example.googletagmanagerwithannotation.models.BadgeBundler
import com.example.googletagmanagerwithannotation.models.Shop
import com.example.googletagmanagerwithannotation.models.ShopBundler
import com.google.firebase.analytics.FirebaseAnalytics

@SuppressWarnings
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val badge1 = Badge(
            0,
            arrayListOf("Icon 1", "Icon 2", "Icon 3"),
            mapOf("icon 1" to "icon 2", "icon 2" to "icon 3")
        )
        val badge2 = Badge(
            1,
            arrayListOf("Icon 4", "Icon 5", "Icon 5"),
            mapOf("icon 4" to "icon 5", "icon 5" to "icon 6")
        )
        val shop = Shop(
            "0",
            listOf(badge1, badge2),
            ""
        )
        val product =
            com.example.googletagmanagerwithannotation.enhancedecommerce.models.ecommerce.Product(
                "0",
                "Product 1",
                "Category 1",
                "Variant 1",
                "Brand 1",
                1000000.0,
                "IDR"
            )

        val badge1Bundle = BadgeBundler.getBundle(badge1)
        val badge2Bundle = BadgeBundler.getBundle(badge2)
        val shopBundle = ShopBundler.getBundle(shop)
        val productBundle = ProductBundler.getBundle(product)
        val productClickBundle =
            ProductClicksBundler.getBundle("Product Clicked", arrayListOf(product))

        val badge1Map = mapOf(
            "icon" to 0,
            "descs" to arrayListOf("Icon 1", "Icon 2", "Icon 3"),
            "descsMap" to mapOf("icon 1" to "icon 2", "icon 2" to "icon 3")
        )

        val badge2Map = mapOf(
            "icon" to 1,
            "descs" to arrayListOf("Icon 4", "Icon 5", "Icon 6"),
            "descsMap" to mapOf("icon 4" to "icon 5", "icon 5" to "icon 6")
        )

        val shopMap = mapOf(
            "id" to "0",
            "item" to listOf(badge1Map, badge2Map),
            "str" to ""
        )

        val productMap = mapOf(
            "item_id" to "0",
            "item_name" to "Product 1",
            "item_category" to "Category 1",
            "item_variant" to "Variant 1",
            "item_brand" to "Brand 1",
            "price" to 100000.0,
            "currency" to "IDR",
            "Items" to shopMap
        )

        val productClickMap = mapOf(
            "item_list" to "Product Clicked",
            "items" to listOf(productMap),
            "shops" to setOf(shopMap)
        )

        val badge1BundleMap = BadgeBundler.getBundle(badge1Map)
        val badge2BundleMap = BadgeBundler.getBundle(badge2Map)
        val shopBundleMap = ShopBundler.getBundle(shopMap)
        val productBundleMap = ProductBundler.getBundle(productMap)
        val productClickBundleMap = ProductClicksBundler.getBundle(productClickMap)

        FirebaseAnalytics.Param.ITEM_ID
    }

    infix fun Bundle.isEqualTo(that: Bundle): Boolean {
        if (that.size() != this.size() || !that.keySet().containsAll(this.keySet())) {
            return false
        }

        this.keySet().forEach {
            if (this[it] is Bundle) return this[it] as Bundle isEqualTo that[it] as Bundle
        }

        return true
    }
}