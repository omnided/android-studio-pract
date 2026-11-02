package ua.edu.mobile.smartlife

import android.app.Application
import ua.edu.mobile.smartlife.di.AppContainer

/** Клас застосунку зберігає єдиний екземпляр контейнера залежностей. */
class SmartLifeApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
