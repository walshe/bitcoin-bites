package dev.emmett.bitcoinfacts.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import dev.emmett.bitcoinfacts.shared.helloFromSharedModule

/**
 * Task 1.2 milestone: prove the Android app links against and calls the shared
 * KMP module. Replaced by the real navigation shell in task group 6.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Text(text = helloFromSharedModule())
                }
            }
        }
    }
}
