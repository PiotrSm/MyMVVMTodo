package com.homeworkshop.mymvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.homeworkshop.mymvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    //Jest konwencja nazywania wszystkich meteod tak żeby zaczynały się od provide+obiekt który dostarcza
    //Metoda dostarczająca obiekt bazy danych database - musi mieć adnotacje @Provides.
    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)//dodajemy to trochę danych początkowych
        .build()

    @Provides
    fun privideTaskDao(db: TaskDatabase) = db.taskDao()

    //funkcja dostarczająca coroutine do wstrzyknięcia i wykorzystania w innych miejscach aplikacji
    @ApplicationScope
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope