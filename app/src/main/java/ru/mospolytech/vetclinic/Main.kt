package ru.mospolytech.vetclinic

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import ru.mospolytech.vetclinic.data.util.AuthManager
import ru.mospolytech.vetclinic.presentation.navigation.NavGraph
import ru.mospolytech.vetclinic.presentation.theme.VetClinicTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VetClinicTheme {
                NavGraph(
                    navController = rememberNavController(),
                    authManager = authManager
                )
            }
        }
    }
}

@HiltAndroidApp
class VetClinicApp : Application()
