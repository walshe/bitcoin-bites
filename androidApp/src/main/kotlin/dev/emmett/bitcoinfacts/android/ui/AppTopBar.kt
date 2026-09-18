package dev.emmett.bitcoinfacts.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.emmett.bitcoinfacts.android.ui.theme.AppSurfaceWarm

/**
 * Every screen's top bar, in one place so the styling stays consistent: a
 * small orange ₿ brand mark before the title, a subtle warm-tinted
 * background instead of flat neutral gray, and a thin orange accent line
 * along the bottom edge. [showAccentLine] is false where a screen already
 * puts something more meaningful right below the bar (Guided ₿ites' own
 * progress bar) - avoids stacking two orange lines on top of each other.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    showAccentLine: Boolean = true,
) {
    Column {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₿",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(title)
                }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AppSurfaceWarm),
        )
        if (showAccentLine) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
