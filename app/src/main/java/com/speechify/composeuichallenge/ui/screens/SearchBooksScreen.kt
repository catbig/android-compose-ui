// ui/screens/SearchBooksScreen.kt
package com.speechify.composeuichallenge.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.ui.viewmodels.SearchBooksViewModel
import com.speechify.composeuichallenge.ui.viewmodels.SearchBooksUiState

@Composable
fun SearchBooksScreen(
    onBookClick: (String) -> Unit,
    viewModel: SearchBooksViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var textFieldValue by remember { mutableStateOf(TextFieldValue(searchQuery)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Field
        TextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                viewModel.updateSearchQuery(newValue.text)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by book name...") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when (uiState) {
            is SearchBooksUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchBooksUiState.Success -> {
                val books = (uiState as SearchBooksUiState.Success).books
                BookList(
                    books = books,
                    onBookClick = onBookClick
                )
            }

            is SearchBooksUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${(uiState as SearchBooksUiState.Error).message}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BookList(
    books: List<Book>,
    onBookClick: (String) -> Unit
) {
    LazyColumn {
        items(
            items = books,
            key = { it.id }
        ) { book ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {
                BookListItem(
                    book = book,
                    onBookClick = onBookClick
                )
            }
        }
    }
}