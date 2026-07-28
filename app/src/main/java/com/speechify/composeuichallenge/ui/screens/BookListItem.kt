// ui/screens/BookListItem.kt
package com.speechify.composeuichallenge.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.speechify.composeuichallenge.data.Book
import com.speechify.composeuichallenge.ui.utils.createPlaceholderBitmap  // ✅ Import from utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun BookListItem(
    book: Book,
    onBookClick: (String) -> Unit
) {
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(book.imageUrl) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(book.imageUrl)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageBitmap.value = bitmap
            } catch (e: Exception) {
                // Image loading failed - keep null
            }
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 4.dp)
            .clickable { onBookClick(book.id) },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Book Image - 3:4 aspect ratio
            androidx.compose.foundation.Image(
                bitmap = imageBitmap.value?.asImageBitmap() ?: createPlaceholderBitmap(60, 80),
                contentDescription = book.name,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Book Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = book.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
                
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                
                Text(
                    text = "⭐ ${book.rating} (${book.reviewCount} reviews)",
                    style = MaterialTheme.typography.labelSmall
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { onBookClick(book.id) },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}