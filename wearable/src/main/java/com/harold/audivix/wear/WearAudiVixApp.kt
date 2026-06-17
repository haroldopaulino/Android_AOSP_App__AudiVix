package com.harold.audivix.wear

import android.app.Application
import com.harold.audivix.wear.data.repository.WearMediaRepository

class WearAudiVixApp : Application() {
    val repository by lazy { WearMediaRepository() }
}
