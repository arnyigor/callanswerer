package com.arny.callanswerer.di

import android.content.Context
import com.arny.callanswerer.CallAnswererApp

val Context.appComponent: AppComponent
    get() = when (this) {
        is CallAnswererApp -> appComponent
        else -> this.applicationContext.appComponent
    }