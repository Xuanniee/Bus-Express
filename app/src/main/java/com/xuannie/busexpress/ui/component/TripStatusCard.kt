package com.xuannie.busexpress.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xuannie.busexpress.domain.model.ActiveTrip
import com.xuannie.busexpress.domain.model.TripProgress

@Composable
fun TripStatusCard(
    activeTrip: ActiveTrip?,
    tripProgress: TripProgress?,
    simulatedTimeDisplay: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 6.dp,
        backgroundColor = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Trip Status",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF174978)
            )

            if (activeTrip == null) {
                Text(
                    text = "No active trip yet.",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF5C6873),
                    modifier = Modifier.padding(top = 8.dp)
                )
                return@Column
            }

            Text(
                text = "Origin: ${activeTrip.originStopCode}",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF202124),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Destination: ${activeTrip.destinationStopCode}",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF202124)
            )
            Text(
                text = "Simulated Time: $simulatedTimeDisplay",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF174978),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Current Stop: ${tripProgress?.currentStopName ?: tripProgress?.currentStopCode ?: "N.A."}",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF202124)
            )
            Text(
                text = "Next Stop: ${tripProgress?.nextStopName ?: tripProgress?.nextStopCode ?: "N.A."}",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF5C6873)
            )
        }
    }
}