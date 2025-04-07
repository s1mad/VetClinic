package ru.mospolytech.vetclinic.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.mospolytech.vetclinic.data.model.AuthorizationState.AUTHORIZED
import ru.mospolytech.vetclinic.data.model.AuthorizationState.IDLE
import ru.mospolytech.vetclinic.data.model.AuthorizationState.LOADING
import ru.mospolytech.vetclinic.data.model.AuthorizationState.UNAUTHORIZED
import ru.mospolytech.vetclinic.data.util.AuthManager
import ru.mospolytech.vetclinic.presentation.content.auth.AuthContent
import ru.mospolytech.vetclinic.presentation.content.auth.AuthViewModelImpl

@Composable
fun NavGraph(
    navController: NavHostController,
    authManager: AuthManager,
    modifier: Modifier = Modifier,
) {
    val authState by authManager.authState.collectAsState()

    fun replaceAll(destination: ContentDestination) = navController.navigate(destination) {
        popUpTo(0)
        launchSingleTop = true
    }

    LaunchedEffect(authState) {
        when (authState) {
            IDLE -> authManager.checkAndUpdateAuthState()
            LOADING -> replaceAll(ContentDestination.Splash)
            UNAUTHORIZED -> replaceAll(ContentDestination.Auth)
            AUTHORIZED -> replaceAll(ContentDestination.PetInfo)
        }
    }

    NavHost(
        navController = navController,
        startDestination = ContentDestination.Splash
    ) {

        composable<ContentDestination.Splash> {
            Surface {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable<ContentDestination.Auth> {
            AuthContent(
                viewModel = hiltViewModel<AuthViewModelImpl>(),
                modifier = modifier
            )
        }

        composable<ContentDestination.PetInfo> {
            Surface {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                Text("PetInfo")
            }
            }
        }

    }
}