package com.example.busexpress.ui.theme

import android.graphics.Outline
import android.graphics.Path
import android.graphics.drawable.shapes.Shape
import android.util.LayoutDirection
import android.util.Size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density

import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

val NavigationDrawer = RoundedCornerShape(percent = 5)