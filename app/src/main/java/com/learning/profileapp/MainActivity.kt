package com.learning.profileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.learning.profile.ProfileScreen
import com.learning.profile.ProfileViewModel
import com.learning.profileapp.ui.theme.ProfileAppTheme
import com.learning.profilelist.ProfileListScreen
import com.learning.profilelist.ProfileListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@Serializable
object ProfileListRoute

@Serializable
data class ProfileRoute(
    val profileId: String?,
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProfileAppTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                NavHost(navController = navController, startDestination = ProfileListRoute) {
                    composable<ProfileListRoute> {
                        val viewModel: ProfileListViewModel = hiltViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        val successMessage = it.savedStateHandle.get<String>("success_message")

                        LaunchedEffect(successMessage) {
                            successMessage?.let { message ->
                                snackbarHostState.showSnackbar(message)
                                it.savedStateHandle.remove<String>("success_message")
                            }
                        }

                        ProfileListScreen(
                            uiState = uiState,
                            snackbarHostState = snackbarHostState,
                            onAddProfileClick = {
                                navController.navigate(ProfileRoute(profileId = null))
                            },
                            onProfileClick = { profileId ->
                                navController.navigate(ProfileRoute(profileId = profileId))
                            },
                        )
                    }
                    composable<ProfileRoute> { backStackEntry ->
                        val profile = backStackEntry.toRoute<ProfileRoute>()
                        val viewModel: ProfileViewModel = hiltViewModel()
                        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                        ProfileScreen(
                            uiState = uiState,
                            effect = viewModel.effect,
                            profileId = profile.profileId,
                            onBackClick = { successMessage ->
                                if (successMessage != null) {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("success_message", successMessage)
                                }
                                navController.popBackStack()
                            },
                            onEvent = viewModel::onEvent,
                        )
                    }
                }
            }
        }
    }
}
