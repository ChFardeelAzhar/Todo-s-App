package com.example.to_dosapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun CustomCircularCheckbox(
    checked: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedColor: Color = Color.Green,
    uncheckedColor: Color = Color.Gray,
    checkmarkColor: Color = Color.White,
    size: Int = 24 // Define the size of the checkbox
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .padding(4.dp)
            .background(
                color = if (checked.value) checkedColor else uncheckedColor,
                shape = CircleShape // Circular shape
            )
            .clickable { onCheckedChange(!checked.value) },
        contentAlignment = Alignment.Center
    ) {
        if (checked.value) {
            Canvas(modifier = Modifier.size((size * 0.6f).dp)) {
                drawCircle(
                    color = checkmarkColor,
                    radius = size * 0.3f,
                )
            }
        }
    }
}