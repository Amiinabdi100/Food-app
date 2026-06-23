package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun IngredientChip(
    text: String,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        if (onRemove != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $text",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() },
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
