package com.example.andy.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.andy.R
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.aallam.openai.api.run.RunId
import com.example.andy.data.models.CalendarDate
import com.example.andy.data.models.CalendarEvent
import com.example.andy.data.models.SocialMedia
import com.example.andy.data.models.StoredLocation
import com.example.andy.data.models.TrackDetails
import com.example.andy.data.models.WeatherResponse
import com.example.andy.data.models.chats.Message
import com.example.andy.ui.theme.AndyTheme
import com.example.andy.ui.theme.State
import com.example.andy.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import generateNoiseImage
import kotlinx.coroutines.isActive
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@AndroidEntryPoint

class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndyTheme {
                val state by mainActivityViewModel.state.collectAsState()

                MyScreen(
                    showWidget = state.isWidgetLayerVisible,
                    viewModel = mainActivityViewModel,
                )
            }
        }
    }
}

@Composable
fun MyScreen(
    showWidget: Boolean = true,
    viewModel: MainActivityViewModel) {
    var state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val messages = viewModel.messages.collectAsState()
    val weather = viewModel.weather.collectAsState()
    val socials = viewModel.socialMedia.collectAsState()
    val userDetail = viewModel.userProfile.collectAsState()
    val currentTrack = viewModel.currentTrackDetails.collectAsState()
    val currentModel = viewModel.currentModel.collectAsState()

    // Handle back button press based on widget toggle
    BackHandler {
        if (state.value.isWidgetLayerVisible)
        {
            (context as Activity).finish()
        } else {
            viewModel.onBackPressedInChat()
            viewModel.clearNewMessage()
            focusManager.clearFocus()
        }
    }

    // 1) Get screen size in pixels
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx().toInt() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx().toInt() }

    // 2) Generate noise once for this screen size
    val noiseBitmap = remember(screenWidthPx, screenHeightPx) {
        generateNoiseImage(screenWidthPx, screenHeightPx)
    }
    Box( modifier = Modifier
        .fillMaxSize())
    {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        hexToColor("BAD8B6"),
                        hexToColor("E1EACD"),
                        hexToColor("D8EFD3"),
                        hexToColor("F9F6E6"),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
            )
        }

        Image(
            bitmap = noiseBitmap,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.08f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .wrapContentHeight(Alignment.Bottom)) {
                    if (state.value.isWidgetLayerVisible)
                    {
                        WidgetLayerTopBar(userName = userDetail.value?.name.toString())
                    } else
                    {
                        ChatLayerTopBar(onModelChanged = {viewModel.modelChange()}, modelName = currentModel.value?.name.toString(), onClearMessages = { viewModel.clearMessages() })
                    }
                }

            },
            bottomBar = {
                MessageEntry(
                    entryText = viewModel.newMessage.collectAsState().value,
                    state = viewModel.state.collectAsState().value,
                    onEntryTextChanged = {viewModel.updateMessage(it)},
                    onSendClicked = {viewModel.sendQuery()},
                    onEntryFocusChanged = {viewModel.entryFocusEvent(it)},
                    modifier = Modifier
                        .padding(horizontal = 16.dp))
            },
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
        )
        { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {

                Column(
                    modifier = Modifier
                        .padding(19.dp)
                        .fillMaxSize()
                ) {
                    // Top portion
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (showWidget) {
                            // Pass in the user name if you have it from VM
                            WidgetLayer(
                                weather.value,
                                currentTrack.value,
                                socials = socials.value,
                                onSeekMusic = { viewModel.seekSpotifyTracks(it) },
                                currentStoredLocation = viewModel.currentStoredLocation.collectAsState().value,
                                nextEvent = viewModel.nextEvent.collectAsState().value,
                                currentDate = viewModel.currentDate.collectAsState().value)
                        } else {
                            ChatLayer(userDetail.value?.name.toString().lowercase(), messages.value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetLayerTopBar( userName: String) {
    Text(
        text = "Hi $userName,",
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        modifier = Modifier.padding(vertical = 25.dp)
    )
}

@Composable
fun WidgetLayer(
    weather: WeatherResponse?,
    trackDetails: TrackDetails?,
    socials: SocialMedia?,
    currentStoredLocation: StoredLocation?,
    nextEvent: CalendarEvent?,
    currentDate: CalendarDate?,
    onSeekMusic: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        // Weather row
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically ) {
            val temp = weather?.hourly?.temperature_2m?.first()?.roundToInt().toString()
            Text(
                text = "${temp}°",
                fontSize = 70.sp,
                fontWeight = FontWeight.Thin,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .wrapContentHeight(Alignment.Bottom)
            )

            Column (verticalArrangement = Arrangement.spacedBy(8.dp)){
                Text(
                    text = currentStoredLocation?.placeName.toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 20.sp
                )
                Text(
                    text = currentStoredLocation?.placeCity.toString(),
                    color = Color.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = currentStoredLocation?.placeCountry.toString(),
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // Calendar Widget
            WidgetBox(
                iconContent = {
                },
                colorGradientEnd = 0xFFD8EFD3,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column {
                    Row (
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(0.6f)) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(0.75f))
                        {
                            Text(
                                text = "Monday",
                                fontSize = 20.sp,
                                modifier = Modifier.weight(1f))
                            Text(
                                text = "March, 2025",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f))
                            Text(
                                text = "Next up (${nextEvent?.duration}H)",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(Alignment.Bottom))
                        }
                        Box(
                            modifier = Modifier
                            .background(shape = RoundedCornerShape(6.dp), color = Color(0xFFCEDF9F).copy(alpha = 0.4f)),

                        ) {
                            Text(
                                text = currentDate?.day.toString(),
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(5.dp))
                        }
                    }
                    Row (
                        horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = nextEvent?.event.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 25.sp,
                            modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Default.Add, // Use Material icon
                            contentDescription = "Add event",
                            tint = Color.Black, // Change color
                            modifier = Modifier
                                .clickable(onClick = {
                                    context.launchApp("com.google.android.calendar")
                                })
                                .size(24.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // X widget
            WidgetBox(
                iconContent = {
                    IconBox(R.drawable.x_logo, 20)
                },
                colorGradientEnd = 0xFFC0C78C,
                modifier = Modifier
                    .clickable(onClick = {
                        context.launchApp("com.twitter.android")
                    })
                    .size(150.dp) // fixed square size
            ) {
                Column (modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center) {
                    Text(text="Posts (${socials?.twitter?.recent_posts?.size})")
                    Text(
                        text = socials?.twitter?.recent_posts?.first().toString(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(Alignment.CenterVertically))
                    Text(text=socials?.twitter?.handle.toString(), modifier = Modifier.align(Alignment.End))

                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First box: album art
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .background(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.1f)
                    ),
            ) {
                AsyncImage(
                    model = trackDetails?.albumArtUrl,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    onState = { state ->
                        if (state is AsyncImagePainter.State.Error) {
                            // handle error, e.g. show placeholder
                        }
                    }
                )
            }

            // Second box: track details and controls
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .weight(0.7f)
                    .fillMaxHeight()
                    .background(
                        color = Color(0xFFF0F0F0).copy(alpha = 0.6f), // for example
                        shape = RoundedCornerShape(8.dp),
                    )
            ) {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))) {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFF1F3C2).copy(alpha = 0.6f)
                            ),
                            start = Offset(0f, 1f),
                            end = Offset(size.width, size.height)
                        )
                    )
                }
                Box(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp))
                {
                    IconBox(R.drawable.spotify_logo)
                }

                Column(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                )
                {
                    Text(
                        text = "Evening Chills",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                    // Track title
                    Text(
                        text = trackDetails?.track.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                    // Artist name
                    Text(
                        text = trackDetails?.artist.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Color(0xFFCEDF9F),
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomEnd = 8.dp,
                                bottomStart = 8.dp
                            )
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        IconBox(R.drawable.left_seek, 18, onClick = { onSeekMusic(false) })
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        IconBox(R.drawable.play, 20,
                            onClick = {
                                context.openSongInSpotify(trackDetails?.uri.toString())
                            })

                    }
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        IconBox(R.drawable.right_seek, 18, onClick = { onSeekMusic(true) })
                    }
                }
            }

        }

    }
}


@Composable
fun IconBox(
    imageId: Int,
    size: Int = 25,
    onClick: () -> Unit = {}
) {
    Box() {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = "Spotify",
            modifier = Modifier
                .clickable(onClick = onClick)
                .wrapContentWidth()
                .size(size.dp))
    }
}

/**
 * A reusable composable box that shows an icon at top-left and
 * allows injecting custom content below/alongside it.
 */
@Composable
fun WidgetBox(
    iconContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    colorGradientEnd: Long,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = Color(0xFFF0F0F0).copy(alpha = 0.6f), // for example
                shape = RoundedCornerShape(8.dp),
            )
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(colorGradientEnd).copy(alpha = 0.6f)
                    ),
                    start = Offset(0f, 1f),
                    end = Offset(size.width, size.height)
                )
            )
        }
        Box(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp))
        {
            iconContent()
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp))
        {
            content()
        }

    }
}

@Composable
fun ChatLayer(
    userName: String,
    messages: List<Message> = listOf<Message>(Message(role = "user", content = "Hi how are you?"))
) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        ) {
        items(messages) { message ->
            when (message.role) {
                Constants.ROLE_USER -> UserMessageTemplate(message, userName)
                Constants.ROLE_ASSISTANT-> AssistantMessageTemplate(message)
                else -> DefaultMessageTemplate(message)
            }
        }
    }
}

@Composable
fun ChatLayerTopBar(
    onModelChanged: () -> Unit,
    onClearMessages: () -> Unit,
    modelName: String
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
    {
        var offsetX by remember { mutableStateOf(0f) }

        val titleText = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("andy")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Thin)) {
                append("@")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Thin)) {
                append(modelName)
            }
        }

        Image(
            painter = painterResource(id = R.drawable.andy_bw),

            contentDescription = "Send button",
            modifier = Modifier
                .border(1.dp, Color.Black, CircleShape)
                .wrapContentWidth()
                .size(50.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onClearMessages() },
                    )
                }
        )
        Text(
            text = titleText,
            fontSize = 22.sp,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    onDragStopped = {
                        if (offsetX > 50) {
                            onModelChanged()
                        }
                        offsetX = 0f
                    },
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    }
                ),
        )
    }
}

@Composable
fun UserMessageTemplate(message: Message, userName: String) {
    Column (
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "@${userName}:",
            color = Color.Black,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = message.content ?: "",
            color = Color(0xFF5A6C57),
            fontSize = 16.sp
        )
    }
}

@Composable
fun AssistantMessageTemplate(message: Message) {
    Column (
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "@andy:",
            color = Color(0xFF729762),
            fontSize = 16.sp
        )
        Text(
            text = message.content ?: "",
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}

// Fallback composable in case message role is unknown.
@Composable
fun DefaultMessageTemplate(message: Message) {
}

@Composable
fun MessageEntry(
    entryText: String,
    state: State,
    onEntryTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    onEntryFocusChanged: (FocusState) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Get the current configuration to determine screen dimensions.
    val configuration = LocalConfiguration.current

    // Calculate maximum entry height as 30% of the screen height.
    val maxEntryHeight = (configuration.screenHeightDp * 0.3f).dp

    val initialEntryHeight = remember { (configuration.screenWidthDp * 0.12f).dp }

    Row(
        modifier = modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
    ) {
        // Keep track of whether the field is focused.
        var isFocused by remember { mutableStateOf(false) }
        // Determine container background color based on focus.
        val containerColor = if (isFocused) Color.White.copy(alpha = 0.5f) else Color(0xFFF1F3C2).copy(alpha = 0.4f)
        // Determine text color based on focus.
        val textColor = if (isFocused) Color.Black else Color.Gray

        // 1) Create an infinite transition that runs forever
        val infiniteTransition = rememberInfiniteTransition()

        // 2) Animate a float from 0f to 360f repeatedly
        val infiniteAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        // 3) Remember the angle offset to preserve rotation when state.isBusy changes
        var angleOffset by remember { mutableStateOf(0f) }
        var lastBusyValue by remember { mutableStateOf(state.isBusy) }

        // 4) When state.isBusy changes from true to false, store the last angle in angleOffset
        LaunchedEffect(state.isBusy) {
            if (lastBusyValue && !state.isBusy) {
                angleOffset += infiniteAngle
                // Optionally ensure it stays in [0..360): angleOffset %= 360f
            }
            lastBusyValue = state.isBusy
        }

        // 5) Compute the displayed angle: 
        //    - If busy, offset + current infiniteAngle
        //    - If not busy, just the offset
        val displayedAngle = if (state.isBusy) angleOffset + infiniteAngle else angleOffset

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .heightIn(initialEntryHeight, max = maxEntryHeight)
                .background(containerColor, shape = RoundedCornerShape(10.dp))
        ) {
            if (!state.isWidgetLayerVisible) {
                Text(
                    text = "@${state.userName.lowercase()}:",
                    color = Color(0xFF729762),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentHeight(
                            Alignment.CenterVertically
                        )
                )
            }

            // Walk around for centering Text input field: https://github.com/JetBrains/compose-multiplatform/issues/202#issuecomment-1423358689
            BasicTextField(
                value = entryText,
                onValueChange = onEntryTextChanged,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .heightIn(1.dp, max = maxEntryHeight)
                    .align(Alignment.CenterVertically)
                    .onFocusChanged { focusState ->
                        onEntryFocusChanged(focusState)
                        isFocused = focusState.isFocused
                    },
                decorationBox =
                    { innerTextField ->
                        if (state.isWidgetLayerVisible)
                        {
                            Text(
                                text = "how can help you today?",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .wrapContentSize(Alignment.CenterStart)
                            )
                        }
                        innerTextField()
                    }
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(initialEntryHeight)
                .background(Color(0xFFCEDF9F), shape = RoundedCornerShape(10.dp))
                .clickable
                {
                    onSendClicked()
                },
        ) {
            Image(
                painter = painterResource(id = R.drawable.send_icon),
                contentDescription = "Send button",
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.Center)
                    .graphicsLayer(rotationZ = displayedAngle)
            )
        }
    }
}

fun hexToColor(hex: String): Color {
    return Color(android.graphics.Color.parseColor("#$hex"))
}

fun Context.openSongInSpotify(trackUrl: String) {
    // Spotify’s package name
    val spotifyPackageName = "com.spotify.music"

    // Create an intent with ACTION_VIEW for the given track URL
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(trackUrl)

        setPackage(spotifyPackageName)
    }

    // Check if Spotify is installed
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        // Handle the case where Spotify isn't installed
        // For example, show a toast or redirect to Play Store
        // Here we’ll just show a Toast:
        Toast.makeText(this, "Spotify is not installed", Toast.LENGTH_SHORT).show()
    }
}

fun Context.launchApp(packageName: String) {
    try {
        // Get the launch intent for the package
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        // If an intent is found, start the activity
        launchIntent?.let {
            // Add flags to start a new task if launching from a non-activity context
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
        } ?: run {
            // If no launch intent is found
            Toast.makeText(
                this,
                "App not found or cannot be launched",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        // Handle any unexpected errors
        Toast.makeText(
            this,
            "Error launching app: ${e.localizedMessage}",
            Toast.LENGTH_SHORT
        ).show()
    }
}