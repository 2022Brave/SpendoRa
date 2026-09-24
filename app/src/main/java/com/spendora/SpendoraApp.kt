package com.spendora

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spendora.data.viewmodel.FinanceViewModel
import com.spendora.navigation.SpendoraDestination
import com.spendora.screens.add.AddTransactionScreen
import com.spendora.screens.analytics.AnalyticsScreen
import com.spendora.screens.home.HomeScreen
import com.spendora.screens.splash.SplashScreen
import com.spendora.screens.splitmate.SplitMateScreen
import com.spendora.screens.transactions.TransactionsScreen
import com.spendora.ui.components.SpendoraBottomBar
import com.spendora.ui.components.SpendoraTopHeader
import com.spendora.ui.theme.SpendoraTheme
import com.spendora.ui.theme.SpendoraThemeMode

/**
 * SPENDO₹A Main Application Shell
 *
 * Implements:
 * - Centralized Black + Purple (default) and White + Purple theme
 * - Cinematic splash screen with restrained reveal
 * - Top Header with branding and tagline
 * - Exactly ONE floating pill bottom navigation container
 * - 5 Destinations: Home | Transactions | + | Analytics | SplitMate
 * - Robust back navigation stack for Android gestures
 * - Real offline-first Room database connection via FinanceViewModel
 */
@Composable
fun SpendoraApp(
    viewModel: FinanceViewModel = viewModel(
        factory = FinanceViewModel.Factory(SpendoraApplication.instance.repository)
    )
) {
    var themeMode by remember { mutableStateOf(SpendoraThemeMode.BLACK_PURPLE) }
    var isSplashVisible by remember { mutableStateOf(true) }
    var currentDestination by remember { mutableStateOf(SpendoraDestination.HOME) }
    val backStack = remember { mutableStateListOf<SpendoraDestination>() }

    val navigateTo: (SpendoraDestination) -> Unit = { dest ->
        if (dest != currentDestination) {
            backStack.add(currentDestination)
            currentDestination = dest
        }
    }

    // Android Back Navigation Handling
    BackHandler(enabled = !isSplashVisible && (backStack.isNotEmpty() || currentDestination != SpendoraDestination.HOME)) {
        if (backStack.isNotEmpty()) {
            val previous = backStack.removeAt(backStack.lastIndex)
            currentDestination = previous
        } else if (currentDestination != SpendoraDestination.HOME) {
            currentDestination = SpendoraDestination.HOME
        }
    }

    SpendoraTheme(themeMode = themeMode) {
        if (isSplashVisible) {
            SplashScreen(
                onSplashFinished = { isSplashVisible = false }
            )
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                // Adaptive width constraint for tablets / large screens
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 640.dp)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        topBar = {
                            SpendoraTopHeader(
                                themeMode = themeMode,
                                onToggleTheme = {
                                    themeMode = if (themeMode == SpendoraThemeMode.BLACK_PURPLE) {
                                        SpendoraThemeMode.WHITE_PURPLE
                                    } else {
                                        SpendoraThemeMode.BLACK_PURPLE
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            // EXACTLY ONE floating pill-style bottom bar container
                            SpendoraBottomBar(
                                currentDestination = currentDestination,
                                onNavigate = navigateTo
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            AnimatedContent(
                                targetState = currentDestination,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "screenTransition"
                            ) { destination ->
                                when (destination) {
                                    SpendoraDestination.HOME -> {
                                        HomeScreen(
                                            viewModel = viewModel,
                                            onNavigateToAdd = {
                                                navigateTo(SpendoraDestination.ADD_TRANSACTION)
                                            }
                                        )
                                    }

                                    SpendoraDestination.TRANSACTIONS -> {
                                        TransactionsScreen(
                                            viewModel = viewModel,
                                            onNavigateToAdd = {
                                                navigateTo(SpendoraDestination.ADD_TRANSACTION)
                                            }
                                        )
                                    }

                                    SpendoraDestination.ADD_TRANSACTION -> {
                                        AddTransactionScreen(
                                            viewModel = viewModel,
                                            onDismiss = {
                                                if (backStack.isNotEmpty()) {
                                                    currentDestination = backStack.removeAt(backStack.lastIndex)
                                                } else {
                                                    currentDestination = SpendoraDestination.HOME
                                                }
                                            }
                                        )
                                    }

                                    SpendoraDestination.ANALYTICS -> {
                                        AnalyticsScreen(
                                            viewModel = viewModel
                                        )
                                    }

                                    SpendoraDestination.SPLITMATE -> {
                                        SplitMateScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
