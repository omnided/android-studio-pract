package ua.edu.mobile.smartlife.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.edu.mobile.smartlife.data.model.HealthRecord
import ua.edu.mobile.smartlife.data.model.RecordType
import ua.edu.mobile.smartlife.data.model.formattedDate
import ua.edu.mobile.smartlife.data.model.formattedValue

/** Іконка для кожного типу показника. */
val RecordType.icon: ImageVector
    get() = when (this) {
        RecordType.WATER -> Icons.Filled.WaterDrop
        RecordType.PULSE -> Icons.Filled.Favorite
        RecordType.SLEEP -> Icons.Filled.Bedtime
        RecordType.WEIGHT -> Icons.Filled.MonitorWeight
    }

/** Картка запису, що повторюється на головному екрані та в журналі. */
@Composable
fun RecordCard(record: HealthRecord, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = record.type.icon,
                contentDescription = record.type.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(record.type.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    record.formattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (record.note.isNotBlank()) {
                    Text(record.note, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Text(
                record.formattedValue(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
