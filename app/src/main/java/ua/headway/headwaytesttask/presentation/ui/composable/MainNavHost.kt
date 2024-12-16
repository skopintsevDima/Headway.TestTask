package ua.headway.headwaytesttask.presentation.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.headway.booksummary.presentation.ui.screen.booksummary.BookSummaryScreen
import ua.headway.headwaytesttask.presentation.resources.Constants.NavRoutes.ROUTE_ID_BOOK_SUMMARY_SCREEN

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_ID_BOOK_SUMMARY_SCREEN,
        modifier = Modifier.padding(padding)
    ) {
        composable(ROUTE_ID_BOOK_SUMMARY_SCREEN) {
            BookSummaryScreen()
        }
    }
}