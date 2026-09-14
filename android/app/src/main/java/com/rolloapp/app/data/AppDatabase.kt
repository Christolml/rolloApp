package com.rolloapp.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [PaperPackage::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun paperPackageDao(): PaperPackageDao

    companion object {
        private const val DATABASE_NAME = "rolloapp.db"

        /**
         * Agrega la foto opcional del paquete. Nullable, sin `DEFAULT`: Room siempre
         * especifica todas las columnas en sus INSERT generados, así que alcanza con
         * que la columna sea nullable (igual que `fotoPath: String? = null` en la
         * entidad). Sin esta migración, subir `version` haría crashear la app en el
         * próximo arranque sobre una base ya existente en el dispositivo del usuario.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE paper_packages ADD COLUMN fotoPath TEXT")
            }
        }

        /**
         * Crea la base de datos. Se llama una sola vez desde `RolloApplication`,
         * que es quien mantiene la única instancia viva de la app.
         */
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME,
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
