package com.example.rma1.views.core.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterButton(
    onClick: () -> Unit,
    appliedFilters: Int,
){
    Box{
        Button(
            onClick = onClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            Text("Filters")
        }

        if(appliedFilters > 0){
            Box(
                modifier = Modifier
                    .align (Alignment.TopEnd)
                    .offset(x = (-16).dp)
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = appliedFilters.toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}