package com.example.googletagmanagerwithannotation

import android.os.Bundle

interface Bundler {
    companion object {
        fun getBundle(obj: Object): Bundle = Bundle()
        fun getBundle(obj: Map<String, Any>): Bundle = Bundle()
    }
}