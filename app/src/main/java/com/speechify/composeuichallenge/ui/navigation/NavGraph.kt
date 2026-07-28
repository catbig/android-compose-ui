// ui/navigation/NavGraph.kt
package com.speechify.composeuichallenge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.speechify.composeuichallenge.ui.screens.BookDetailsScreen
import com.speechify.composeuichallenge.ui.screens.SearchBooksScreen

sealed class Screen(val route: String) {
    object SearchBooks : Screen("search_books")
    object BookDetails : Screen("book_details/{bookId}") {
        fun passBookId(bookId: String) = "book_details/$bookId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.SearchBooks.route
    ) {
        composable(Screen.SearchBooks.route) {
            SearchBooksScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetails.passBookId(bookId))
                }
            )
        }
        
        composable(
            route = Screen.BookDetails.route,
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
            BookDetailsScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}