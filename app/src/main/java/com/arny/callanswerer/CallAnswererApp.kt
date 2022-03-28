package com.arny.callanswerer

import android.app.Application
import com.arny.callanswerer.di.AppComponent
import com.arny.callanswerer.di.AppModule
import com.arny.callanswerer.di.DaggerAppComponent

class CallAnswererApp : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}