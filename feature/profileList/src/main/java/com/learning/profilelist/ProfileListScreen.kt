package com.learning.profilelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileListScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileListUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onAddProfileClick: () -> Unit,
    onProfileClick: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = modifier.fillMaxWidth(),
                title = {
                    Text(text = "Profiles")
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProfileClick,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add profile",
                )
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = uiState.errorMessage)
                }
            }

            uiState.profiles.isEmpty() -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "No profiles found")
                }
            }

            else -> {
                ProfileListContent(modifier = Modifier.padding(innerPadding), uiState = uiState, onProfileClick = onProfileClick)
            }
        }
    }
}

@Composable
private fun ProfileListContent(
    modifier: Modifier = Modifier,
    uiState: ProfileListUiState,
    onProfileClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = uiState.profiles,
            key = { profile -> profile.id },
        ) { profile ->

            ProfileListItem(
                profile = profile,
                onClick = {
                    onProfileClick(profile.id)
                },
            )
        }
    }
}
