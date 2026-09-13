package com.rolloapp.app

import android.app.Application
import com.rolloapp.app.data.AppDatabase
import com.rolloapp.app.data.PaperRepository

/**
 * Contenedor de dependencias de la app, sin frameworks de DI.
 *
 * Crea una única instancia de [AppDatabase] y de [PaperRepository] para todo el
 * proceso; el ViewModel las recibe a través de una factory manual.
 */
class RolloApplication : Application() {

    private val database: AppDatabase by lazy { AppDatabase.build(this) }

    val repository: PaperRepository by lazy { PaperRepository(database.paperPackageDao()) }
}
