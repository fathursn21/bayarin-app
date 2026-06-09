package id.ac.pnm.bayarin_app

import android.app.Application

class ContextApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        lateinit var instance: ContextApplication
            private set
    }
}