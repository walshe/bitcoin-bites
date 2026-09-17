package dev.emmett.bitcoinfacts.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.emmett.bitcoinfacts.android.data.AppContainer
import dev.emmett.bitcoinfacts.android.notifications.EXTRA_FACT_ID
import dev.emmett.bitcoinfacts.android.ui.FactDetailScreen
import dev.emmett.bitcoinfacts.android.ui.FactListScreen
import dev.emmett.bitcoinfacts.android.ui.FavoritesScreen
import dev.emmett.bitcoinfacts.android.ui.HomeScreen
import dev.emmett.bitcoinfacts.android.ui.JourneyScreen
import dev.emmett.bitcoinfacts.android.ui.PurchaseBitcoinScreen
import dev.emmett.bitcoinfacts.android.ui.theme.BitcoinFactsTheme
import dev.emmett.bitcoinfacts.shared.history.JourneyMode
import dev.emmett.bitcoinfacts.taxonomy.Category

private const val ROUTE_HOME = "home"
private const val ROUTE_LIST = "list"
private const val ROUTE_FAVORITES = "favorites"
private const val ROUTE_DETAIL = "detail/{factId}"
private const val ROUTE_JOURNEY = "journey"
private const val ROUTE_PURCHASE = "purchase"

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op either way */ }

    // Task 8.2: a plain mutableStateOf, not `by remember` inside onCreate - it must
    // survive as a property so onNewIntent (called when the activity is already
    // running - the common case for a notification tap) can update it too. Without
    // this, a deep-link tap while the app is already alive was silently dropped:
    // the system delivers it to onNewIntent, never re-runs onCreate, and the old
    // code only ever read the intent extra once, in onCreate.
    private val deepLinkFactId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as BitcoinFactsApplication).container
        // "Opened the app" counts toward the streak the same as receiving a daily fact (task 4.5/7.3).
        container.appOpenLogStore.recordOpenToday()

        // Task 8.3: optional permission, requested once on first launch - the app
        // is fully usable without it (see showDailyFactNotification's own check).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        deepLinkFactId.value = intent.getStringExtra(EXTRA_FACT_ID)

        setContent {
            BitcoinFactsTheme {
                BitcoinFactsNavHost(container, deepLinkFactId.value)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkFactId.value = intent.getStringExtra(EXTRA_FACT_ID)
    }
}

@Composable
private fun BitcoinFactsNavHost(container: AppContainer, deepLinkFactId: String?) {
    val navController = rememberNavController()
    var selectedCategories by remember { mutableStateOf(setOf<Category>()) }
    // Separate from selectedCategories: "Random Bites" and "Guided Bites" are
    // always unfiltered by definition, while "Start Journey" (from the list
    // screen) captures whatever the list's filter happened to be at that
    // moment - neither should leak into the other via a single shared variable.
    var journeyCategories by remember { mutableStateOf(setOf<Category>()) }
    // "Start Journey" and "Random Bites" both shuffle; only "Guided Bites" walks
    // facts in their canonical `order` instead. Also controls resume-position
    // persistence (null for "Start Journey" - its filtered scope varies run to
    // run, so there's no single position worth remembering).
    var journeyMode by remember { mutableStateOf<JourneyMode?>(null) }
    var favoriteIds by remember { mutableStateOf(container.favoritesStore.allFavoriteIds().toSet()) }
    val streak = remember { container.appOpenLogStore.currentStreak() }

    fun toggleFavorite(factId: String) {
        val nowFavorite = factId !in favoriteIds
        container.favoritesStore.setFavorite(factId, nowFavorite)
        favoriteIds = container.favoritesStore.allFavoriteIds().toSet()
    }

    LaunchedEffect(deepLinkFactId) {
        if (deepLinkFactId != null) {
            navController.navigate("detail/$deepLinkFactId")
        }
    }

    NavHost(navController = navController, startDestination = ROUTE_HOME) {
        composable(ROUTE_HOME) {
            HomeScreen(
                streak = streak,
                onBrowseAll = { navController.navigate(ROUTE_LIST) },
                onGuidedJourney = {
                    journeyCategories = emptySet()
                    journeyMode = JourneyMode.GUIDED
                    navController.navigate(ROUTE_JOURNEY)
                },
                onRandomJourney = {
                    journeyCategories = emptySet()
                    journeyMode = JourneyMode.RANDOM
                    navController.navigate(ROUTE_JOURNEY)
                },
                onOpenFavorites = { navController.navigate(ROUTE_FAVORITES) },
                onPurchaseBitcoin = { navController.navigate(ROUTE_PURCHASE) },
            )
        }
        composable(ROUTE_LIST) {
            FactListScreen(
                container = container,
                selectedCategories = selectedCategories,
                onToggleCategory = { category ->
                    selectedCategories = if (category in selectedCategories) {
                        selectedCategories - category
                    } else {
                        selectedCategories + category
                    }
                },
                favoriteIds = favoriteIds,
                onToggleFavorite = ::toggleFavorite,
                streak = streak,
                onFactClick = { factId -> navController.navigate("detail/$factId") },
                onOpenFavorites = { navController.navigate(ROUTE_FAVORITES) },
                onBack = { navController.popBackStack() },
                onStartJourney = {
                    journeyCategories = selectedCategories
                    journeyMode = null
                    navController.navigate(ROUTE_JOURNEY)
                },
            )
        }
        composable(ROUTE_JOURNEY) {
            JourneyScreen(
                container = container,
                categories = journeyCategories,
                favoriteIds = favoriteIds,
                onToggleFavorite = ::toggleFavorite,
                onBack = { navController.popBackStack() },
                mode = journeyMode,
            )
        }
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument("factId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val factId = backStackEntry.arguments?.getString("factId") ?: return@composable
            FactDetailScreen(
                container = container,
                factId = factId,
                isFavorite = factId in favoriteIds,
                onToggleFavorite = { toggleFavorite(factId) },
                onBack = { navController.popBackStack() },
                onRelatedFactClick = { relatedId -> navController.navigate("detail/$relatedId") },
            )
        }
        composable(ROUTE_FAVORITES) {
            FavoritesScreen(
                container = container,
                favoriteIds = favoriteIds,
                onBack = { navController.popBackStack() },
                onFactClick = { factId -> navController.navigate("detail/$factId") },
            )
        }
        composable(ROUTE_PURCHASE) {
            PurchaseBitcoinScreen(onBack = { navController.popBackStack() })
        }
    }
}
