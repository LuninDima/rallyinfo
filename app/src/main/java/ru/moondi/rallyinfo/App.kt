package ru.moondi.rallyinfo

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@App)
        appinstance = this
    }

    companion object{
var appinstance: App? = null
        fun getSApplicationContext(): Context {
            return appinstance!!.applicationContext
        }
    }
}