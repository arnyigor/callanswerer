package com.arny.callanswerer.di

import com.arny.callanswerer.presentation.MainFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainFragment: MainFragment)
}