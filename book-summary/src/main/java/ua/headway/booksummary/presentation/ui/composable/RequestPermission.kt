package ua.headway.booksummary.presentation.ui.composable

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import ua.headway.booksummary.R

@Composable
fun RequestPermission(permission: String, onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                Toast.makeText(
                    context,
                    R.string.permission_denied_error_message_to_user,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }
}