package com.harold.audivix

import android.app.Application
import com.harold.audivix.data.repository.AppContainer
import com.harold.audivix.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AudiVixApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AudiVixApp)
            modules(appModules)
        }
        container = AppContainer(this)
    }
}
