package com.rolloapp.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PaperPackage::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paperPackageDao(): PaperPackageDao

    companion object {
        private const val DATABASE_NAME = "rolloapp.db"

        /**
         * Crea la base de datos. Se llama una sola vez desde `RolloApplication`,
         * que es quien mantiene la única instancia viva de la app.
         */
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME,
            ).build()
    }
}
