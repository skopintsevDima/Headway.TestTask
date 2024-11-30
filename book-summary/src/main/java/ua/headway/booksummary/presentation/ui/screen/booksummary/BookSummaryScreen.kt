package ua.headway.booksummary.presentation.ui.screen.booksummary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.headway.booksummary.presentation.ui.resources.LocalResources

// TODO: (EVERYWHERE) Remove all magic constants into Constants
// TODO: Remove MaterialTheme import ---> Apply theme from app module
@Composable
fun ListenBookSummaryScreen() {
    val playbackTime = remember { mutableIntStateOf(28) }
    val totalTime = remember { mutableIntStateOf(132) }
    val playbackSpeed = remember { mutableStateOf("x1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LocalResources.Colors.MilkWhite)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBookCover()
        Spacer(modifier = Modifier.height(48.dp))

        KeyPointTitle()
        Spacer(modifier = Modifier.height(10.dp))

        Subtitle()
        Spacer(modifier = Modifier.height(16.dp))

        PlaybackProgressBar(playbackTime, totalTime)
        Spacer(modifier = Modifier.height(16.dp))

        PlaybackSpeedToggle(playbackSpeed)
        Spacer(modifier = Modifier.height(32.dp))

        PlaybackControls()

        SummaryModeToggle(
            isAudioModeEnabled = true,
            onAudioClick = {},
            onTextClick = {}
        )
    }
}

@Composable
private fun TopBookCover() {
    Box(
        modifier = Modifier
            .height(350.dp)
            .width(250.dp)
            .padding(top = 32.dp)
    ) {
        // TODO: Download image
        Image(
            painter = painterResource(id = LocalResources.Icons.BookCover),
            contentDescription = "Book Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun KeyPointTitle() {
    Text(
        text = "KEY POINT 2 OF 10",
        style = MaterialTheme.typography.body1.copy(
            color = LocalResources.Colors.Gray,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp
        )
    )
}

@Composable
private fun Subtitle() {
    Text(
        modifier = Modifier.padding(horizontal = 8.dp),
        text = "Design is not how a thing looks, but how it works",
        style = MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Normal,
            color = LocalResources.Colors.Black
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PlaybackProgressBar(
    playbackTime: MutableIntState,
    totalTime: MutableIntState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatTime(playbackTime.intValue),
            style = MaterialTheme.typography.caption.copy(
                color = LocalResources.Colors.Gray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
        Slider(
            value = playbackTime.intValue.toFloat(),
            onValueChange = { playbackTime.intValue = it.toInt() },
            valueRange = 0f..totalTime.intValue.toFloat(),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp),
            colors = SliderDefaults.colors(
                thumbColor = LocalResources.Colors.Blue,
                activeTrackColor = LocalResources.Colors.Blue,
                inactiveTrackColor = LocalResources.Colors.LightGray
            )
        )
        Text(
            text = formatTime(totalTime.intValue),
            style = MaterialTheme.typography.caption.copy(
                color = LocalResources.Colors.Gray,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun PlaybackSpeedToggle(playbackSpeed: MutableState<String>) {
    Button(
        onClick = { toggleSpeed(playbackSpeed) },
        colors = ButtonDefaults.buttonColors(backgroundColor = LocalResources.Colors.LightGray),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        contentPadding = PaddingValues(horizontal = 10.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Speed ${playbackSpeed.value}",
            style = MaterialTheme.typography.button.copy(
                color = LocalResources.Colors.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        )
    }
}

@Composable
private fun PlaybackControls() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Handle skip backward */ }) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.SkipBack),
                contentDescription = "Skip Back",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = { /* Handle rewind */ }) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.Replay5),
                contentDescription = "Rewind 5 Seconds",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = { /* Handle pause/play */ }) {
            Icon(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = LocalResources.Icons.Play),
                contentDescription = "Play/Pause",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = { /* Handle fast forward */ }) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.Forward10),
                contentDescription = "Fast Forward 10 Seconds",
                tint = LocalResources.Colors.Black
            )
        }

        IconButton(onClick = { /* Handle skip forward */ }) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = LocalResources.Icons.SkipForward),
                contentDescription = "Skip Forward",
                tint = LocalResources.Colors.Black
            )
        }
    }
}

@Composable
private fun SummaryModeToggle(
    isAudioModeEnabled: Boolean,
    onAudioClick: () -> Unit,
    onTextClick: () -> Unit
) {
    val isAudioMode = remember { mutableStateOf(isAudioModeEnabled) }

    val rowShape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .padding(top = 32.dp)
            .clip(rowShape)
            .background(LocalResources.Colors.White)
            .border(width = 1.dp, color = LocalResources.Colors.LightGray, shape = rowShape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeToggleIconButton(
            isSelected = isAudioMode.value,
            onClick = {
                isAudioMode.value = true
                onAudioClick()
            },
            icon = ImageVector.vectorResource(LocalResources.Icons.Headset),
            description = "Summary audio"
        )

        ModeToggleIconButton(
            isSelected = !isAudioMode.value,
            onClick = {
                isAudioMode.value = false
                onTextClick()
            },
            icon = ImageVector.vectorResource(LocalResources.Icons.List),
            description = "Summary text"
        )
    }
}

@Composable
private fun ModeToggleIconButton(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    description: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                color = LocalResources.Colors.Blue.takeIf { isSelected }
                    ?: LocalResources.Colors.White,
                shape = RoundedCornerShape(50)
            )
    ) {
        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = icon,
            contentDescription = description,
            tint = LocalResources.Colors.White.takeIf { isSelected } ?: LocalResources.Colors.Black,
        )
    }
}

@Composable
fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

fun toggleSpeed(playbackSpeed: MutableState<String>) {
    playbackSpeed.value = when (playbackSpeed.value) {
        "x1" -> "x1.5"
        "x1.5" -> "x2"
        "x2" -> "x1"
        else -> "x1"
    }
}

@Preview(showBackground = true)
@Composable
private fun ListenBookSummaryPreview() {
    MaterialTheme {
        ListenBookSummaryScreen()
    }
}
