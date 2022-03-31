package com.arny.callanswerer.presentation.extentions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> T?.safeWith(init: T.() -> Unit = {}) {
    this?.let { with(it) { init() } }
}

fun Fragment.checkPermissions(permissions: Array<String>): Boolean {
    var result: Int
    val listPermissionsNeeded = mutableListOf<String>()
    for (p in permissions) {
        result = ContextCompat.checkSelfPermission(requireContext(), p)
        if (result != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(p)
        }
    }
    return listPermissionsNeeded.isEmpty()
}

fun <T> Fragment.requestPermission(
    resultLauncher: ActivityResultLauncher<T>,
    permission: String,
    input: T,
    checkPermissionOk: (input: T) -> Unit = {}
) {
    when {
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            checkPermissionOk(input)
        }
        shouldShowRequestPermissionRationale(permission) -> {
            resultLauncher.launch(input)
        }
        else -> {
            resultLauncher.launch(input)
        }
    }
}

fun Intent?.dump(): String? {
    val bundle = this?.extras
    if (bundle != null) {
        val stringBuilder = StringBuilder()
        for (key in bundle.keySet()) {
            val value = bundle[key]
            if (value != null) {
                stringBuilder.append(
                    String.format("class(%s) %s->'%s'", value.javaClass.name, key, value.toString())
                )
            }
        }
        return stringBuilder.toString()
    }
    return null
}