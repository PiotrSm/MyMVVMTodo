package com.homeworkshop.mymvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homeworkshop.mymvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    // Dao jest dostarczane przez injection
    abstract fun taskDao(): TaskDao

    //Klasa dodająca jakieś dane początkowe wywoływane w chwili tworzenia bazy danych
    //@Inject constructor działa tak samo jak @Provides ale Inject możemy używac z naszymi własnymi klasami które chcemy wstrzykiwać
    class Callback @Inject constructor(
        // Jeżeli byśmy chcieli dodać w konstruktorze tylko private val database: TaskDatabase to powoduje skrzyżowanie bo obiekt bazy danych jeszcze nie został stworzony
        // a aby go stworzyć potrzebny jest obiekt callback.
        //Przez opakowanie go w Provider mamy lazy constructing co daje możliwość zaczekania aż obiekt bazy danych zostanie stworzony
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ):RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            //metody dao są suspend i muszą być wywoływane w couritines
            //GlobalScope.launch {  } jest złą praktyką bo daje mało kontroli nad couritines i trudny do testowania
            //zamiast tego dostarczamy couritine utworzony w daggerze i dostępny przez injection
            applicationScope.launch {
                dao.insert(Task("Wash the dishes", completed = true))
                dao.insert(Task("Do the laundry"))
                dao.insert(Task("Buy groceries"))
                dao.insert(Task("Prepare food", important = true))
                dao.insert(Task("Visit grandma", completed = true))
                dao.insert(Task("Repair my bike"))
                dao.insert(Task("Call the doctor"))
            }
        }
    }
}