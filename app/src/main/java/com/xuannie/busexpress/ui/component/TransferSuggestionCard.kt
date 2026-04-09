package com.xuannie.busexpress.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xuannie.busexpress.domain.model.TransferSuggestion

@Composable
fun TransferSuggestionCard(
    suggestion: TransferSuggestion,
    baselineArrivalTime: String,
    transferArrivalTime: String,
    onUseBaseline: () -> Unit,
    onUseTransfer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        backgroundColor = Color.White,
        title = {
            Text(
                text = "Transfer Recommendation",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF202124)
            )
        },
        text = {
            Column {
                Text(
                    text = suggestion.message,
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF202124)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Baseline arrival: $baselineArrivalTime",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF5C6873)
                )

                Text(
                    text = "Transfer arrival: $transferArrivalTime",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF5C6873)
                )

                if (suggestion.timeSavedSeconds > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Estimated saving: ${suggestion.timeSavedSeconds / 60} min",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF174978)
                    )
                }
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onUseBaseline,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFE7F0F7),
                        contentColor = Color(0xFF202124)
                    )
                ) {
                    Text("Don't Transfer")
                }

                Button(
                    onClick = onUseTransfer,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF174978),
                        contentColor = Color.White
                    )
                ) {
                    Text("Transfer")
                }
            }
        }
    )
}