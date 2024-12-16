package ua.headway.headwaytesttask.presentation.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ua.headway.booksummary.presentation.ui.screen.booksummary.BookSummaryScreen
import ua.headway.headwaytesttask.presentation.resources.Constants.NavRoutes.ROUTE_ID_BOOK_SUMMARY_SCREEN
import ua.headway.headwaytesttask.presentation.ui.theme.HeadwayTestTaskTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeadwayTestTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(padding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    padding: PaddingValues = PaddingValues(0.dp)
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

@Preview(showBackground = true)
@Composable
fun MainNavHostPreview() {
    HeadwayTestTaskTheme {
        MainNavHost()
    }
}