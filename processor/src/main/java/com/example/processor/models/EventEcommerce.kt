package com.example.processor.models

class ProductImpressionModel {

    companion object {
        val productImpressionBundle = mapOf(
            "items" to "java.util.ArrayList<com.example.googletagmanagerwithannotation.Product>",
            "ITEM_LIST" to String::class.java.name)
    }

    class ProductModel {
        companion object {
            val productBundle = mapOf(
                "item_id" to String::class.java.name,
                "item_name" to String::class.java.name,
                "item_category" to String::class.java.name,
                "item_variant" to String::class.java.name,
                "item_brand" to String::class.java.name,
                "price" to Double::class.java.name,
                "currency" to String::class.java.name
            )
        }

        class ItemModel {
            companion object {
                val itemBundle = mapOf(
                    "id" to String::class.java.name
                )
            }
        }
    }
}

class ProductClicksModel {

    companion object {
        val productClicksBundle = mapOf(
            "ITEM_LIST" to String::class.java.name,
            "items" to "java.util.ArrayList<com.example.googletagmanagerwithannotation.Product>")
    }

    class ProductModel {
        companion object {
            val productBundle = mapOf(
                "item_id" to String::class.java.name,
                "item_name" to String::class.java.name,
                "item_category" to String::class.java.name,
                "item_variant" to String::class.java.name,
                "item_brand" to String::class.java.name,
                "price" to Double::class.java.name,
                "currency" to String::class.java.name
            )
        }

        class ItemModel {
            companion object {
                val itemBundle = mapOf(
                    "id" to String::class.java.name
                )
            }
        }
    }
}