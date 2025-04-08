package ru.mospolytech.vetclinic.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
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
import ru.mospolytech.vetclinic.data.model.auth.AuthorizationState.AUTHORIZED
import ru.mospolytech.vetclinic.data.model.auth.AuthorizationState.IDLE
import ru.mospolytech.vetclinic.data.model.auth.AuthorizationState.LOADING
import ru.mospolytech.vetclinic.data.model.auth.AuthorizationState.UNAUTHORIZED
import ru.mospolytech.vetclinic.data.util.AuthManager
import ru.mospolytech.vetclinic.presentation.content.auth.AuthContent
import ru.mospolytech.vetclinic.presentation.content.auth.AuthViewModelImpl
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoContent
import ru.mospolytech.vetclinic.presentation.content.pet.PetInfoViewModelImpl

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
            Scaffold { paddingValues ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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
            PetInfoContent(
                petInfoViewModel = hiltViewModel<PetInfoViewModelImpl>(),
                modifier = modifier
            )
        }

    }
}