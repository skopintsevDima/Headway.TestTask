package ua.headway.headwaytesttask.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.res.stringResource
import ua.headway.booksummary.presentation.ui.screen.booksummary.composable.MessageScreen
import ua.headway.core.presentation.ui.resources.LocalResources
import ua.headway.core.presentation.ui.theme.HeadwayTestTaskTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeadwayTestTaskTheme {
                MessageScreen(message = stringResource(LocalResources.Strings.GlobalCrashMessage))
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, CrashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}