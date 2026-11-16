package br.com.herdrive.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RosaHerDrive = Color(0xFFB65372)
val TextoHerDrive = Color(0xFF18181B)

private val CoresHerDrive = lightColorScheme(
    primary = RosaHerDrive,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF9E3EB),
    onPrimaryContainer = TextoHerDrive,
    secondary = Color(0xFF77515F),
    background = Color(0xFFFAF7F8),
    onBackground = TextoHerDrive,
    surface = Color.White,
    onSurface = TextoHerDrive,
    surfaceVariant = Color(0xFFF4EDF0),
    onSurfaceVariant = Color(0xFF51464B)
)

@Composable
fun HerDriveTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CoresHerDrive, content = content)
}
