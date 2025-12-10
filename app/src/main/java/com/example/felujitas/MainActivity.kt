package com.example.felujitas

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.felujitas.data.database.AppDatabase
import com.example.felujitas.data.repository.RenovationRepository
import com.example.felujitas.navigation.RenovationApp
import com.example.felujitas.ui.theme.FelujitasTheme
import com.example.felujitas.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    //több jogosultság egyszerre történő lekérésére szolgál
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        //itt kezelhetők a jogosultság kérések eredményei, ha szükséges
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //szükséges jogosultságok bekérése (kamera, tárhely)
        requestPermissions()

        //adatbázis és a repository inicializálása
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = RenovationRepository(
            roomDao = database.roomDao(),
            taskDao = database.taskDao(),
            materialDao = database.materialDao()
        )

        val viewModelFactory = ViewModelFactory(repository)

        setContent {
            FelujitasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RenovationApp(viewModelFactory = viewModelFactory)
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        //kamera jogosultság számlafotók készítéséhez
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        //tárhely hozzáférés engedélyezése
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        //ha van olyan jogosultság, amit még kérni kell, akkor indítjuk a kérést
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}