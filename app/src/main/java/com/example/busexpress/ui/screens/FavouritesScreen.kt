package com.example.busexpress.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun FavouritesScreen(

) {
    // Variable to remember Tab Row
    var tapRowState by rememberSaveable { mutableStateOf(0) }
    val tapRowTitles = listOf("Going Out", "Coming Back")

    Column() {
        // Navigation Bar for Going Out & Coming Back
        TabRow(
            selectedTabIndex = tapRowState,
            divider = {
                Divider(thickness = 3.dp)
            },
        ) {
            tapRowTitles.forEachIndexed { index, title ->
                Tab(
                    selected = (tapRowState == index),
                    onClick = {
                        tapRowState = index
                    },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) },
                )
            }
        }
        if (tapRowState == 0) {
            Text("Going Out")
        }
        else if (tapRowState == 1) {
            Text("Coming Back")
        }
    }


}


@Composable
fun TravellingDirectionAppBar(
    tapRowTitles: List<String>,
//    tapRowIcons: List<String>,
) {

}













