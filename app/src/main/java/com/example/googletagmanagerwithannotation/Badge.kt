package com.example.googletagmanagerwithannotation

import com.example.annotation.BundleThis

@BundleThis(nameAsKey = true, defaultAll = true)
data class Badge(
    val icon: Int,
    val descs: ArrayList<String>,
    val descsMap: Map<String, String>
)