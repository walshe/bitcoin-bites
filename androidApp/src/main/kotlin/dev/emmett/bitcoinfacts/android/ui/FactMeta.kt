package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.ui.theme.BeginnerGreen
import dev.emmett.bitcoinfacts.android.ui.theme.BitcoinOrange
import dev.emmett.bitcoinfacts.android.ui.theme.ErrorRed
import dev.emmett.bitcoinfacts.shared.model.Fact

/**
 * Category tag pills plus a color-coded difficulty indicator. Shared between
 * [FactDetailScreen] and the Journey screen's per-page card so both show the
 * same metadata the same way. `difficulty` has been part of the content
 * schema since the start but was never actually surfaced in the UI until now.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryAndDifficultyRow(fact: Fact, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        fact.categories.forEach { category ->
            CategoryChip(category.displayName())
        }
        fact.difficulty?.let { difficulty ->
            DifficultyIndicator(difficulty)
        }
    }
}

@Composable
private fun CategoryChip(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun DifficultyIndicator(difficulty: String) {
    val color = when (difficulty) {
        "beginner" -> BeginnerGreen
        "advanced" -> ErrorRed
        else -> BitcoinOrange // "intermediate" and any unrecognized value
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Text(
            text = " " + difficulty.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}
