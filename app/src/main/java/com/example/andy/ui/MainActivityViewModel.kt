package com.example.andy.ui

import android.util.Log
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.andy.data.models.CalendarDate
import com.example.andy.data.models.CalendarEvent
import com.example.andy.data.models.LocationPoint
import com.example.andy.data.models.Playlist
import com.example.andy.data.models.SocialMedia
import com.example.andy.data.models.StoredLocation
import com.example.andy.data.models.TrackDetails
import com.example.andy.data.models.UserProfile
import com.example.andy.data.models.WeatherResponse
import com.example.andy.data.models.args.AddEventArgs
import com.example.andy.data.models.args.AddPlaylistArgs
import com.example.andy.data.models.args.GetWeatherArgs
import com.example.andy.data.models.args.LaunchAppArgs
import com.example.andy.data.models.chats.Function
import com.example.andy.data.models.chats.LLMModel
import com.example.andy.data.models.chats.Message
import com.example.andy.data.models.chats.StructuredChatCompletionRequest
import com.example.andy.data.models.chats.ToolSpec
import com.example.andy.data.repository.CalendarRepository
import com.example.andy.data.repository.GenAIRepository
import com.example.andy.data.repository.LocationRepository
import com.example.andy.data.repository.SocialsRepository
import com.example.andy.data.repository.SpotifyRepository
import com.example.andy.data.repository.UserRepository
import com.example.andy.data.repository.WeatherRepository
import com.example.andy.ui.theme.State
import com.example.andy.util.PromptHelper
import com.example.andy.util.SchemaConfig
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import generateNoiseImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel  @Inject constructor(
    private val gson: Gson,
    private val calendarRepository: CalendarRepository,
    private val socialsRepository: SocialsRepository,
    private val weatherRepository: WeatherRepository,
    private val spotifyRepository: SpotifyRepository,
    private val userRepository: UserRepository,
    private val genAIRepository: GenAIRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var currentTrackIndex = 0
    private var currentModelIndex = 0

    private val dateToday: String = "2024-05-15"
    private val modelList: List<LLMModel> = listOf(
        LLMModel(name = "gpt-4o", model = "gpt-4o-2024-08-06"),
        LLMModel(name = "gpt-o1-mini", model = "o1-mini-2024-09-12"),
    )
    private val aiSuggestedPlaylist = "Evening Chill"
    private var trackDetailsList = listOf<TrackDetails>()

    // View Properties
    // Widget Layer
    val defaultState = State(
        isWidgetLayerVisible = true,
        isBusy = false,
        userName = "")
    private val _state = MutableStateFlow(defaultState)
    val state = _state.asStateFlow()

    private val _lastLocation = MutableStateFlow<LocationPoint?>(null)
    val lastLocation = _lastLocation.asStateFlow()

    private val _nextEvent = MutableStateFlow<CalendarEvent?>(null)
    val nextEvent = _nextEvent.asStateFlow()

    private val _currentDate = MutableStateFlow<CalendarDate?>(null)
    val currentDate = _currentDate.asStateFlow()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private val _currentStoredLocation = MutableStateFlow<StoredLocation?>(null)
    val currentStoredLocation = _currentStoredLocation.asStateFlow()

    private val _currentModel = MutableStateFlow<LLMModel?>(modelList.first())
    val currentModel = _currentModel.asStateFlow()

    private val _calendarEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val calendarEvents = _calendarEvents.asStateFlow()

    private val _currentTrackDetails = MutableStateFlow<TrackDetails?>(null)
    val currentTrackDetails = _currentTrackDetails.asStateFlow()

    private val _socialMedia = MutableStateFlow<SocialMedia?>(null)
    val socialMedia = _socialMedia.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    //TODO:To launch an app from the view model it requires context on the main thread, hence this staeFlow will deliver payload to launch from the main thread
    private val _launchPackage = MutableStateFlow("")
    val launchPackage = _launchPackage.asStateFlow()

    private val _newMessage = MutableStateFlow("")
    val newMessage = _newMessage.asStateFlow()

    // Chat layer
    // List of chat messages with initial system prompt.
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    // Background Noise Image Layer
    private val _noiseImage = MutableStateFlow<ImageBitmap?>(null)
    val noiseImage: StateFlow<ImageBitmap?> = _noiseImage

    // Methods
    init {
        // Generate noise once at initialization
        _noiseImage.value = generateNoiseImage(512, 512)
        fetchCalendarEvents()
        fetchSocialMedia()
        fetchLastLocation()
    }

    fun updateMessage(message: String) {
        _newMessage.value = message
    }

    fun clearNewMessage() {
        _newMessage.value = ""
    }

    fun clearMessages() {
        _messages.update { currentList ->
            currentList.take(1)
        }
    }

    fun entryFocusEvent(event: FocusState) {
        when {
            event.isFocused && _messages.value.isNotEmpty()
                -> {
                _state.value = _state.value.copy(isWidgetLayerVisible = false)
            }
            !event.isFocused && _messages.value.size == 1 -> {
                _state.value = _state.value.copy(isWidgetLayerVisible = true)
            }
        }
    }

    fun onBackPressedInChat() {
        _state.value = state.value.copy(isWidgetLayerVisible = true)
    }

    // Method to process sending a query.
    fun sendQuery() {
        try {
            _state.value = _state.value.copy(isBusy = true)
            val query = _newMessage.value.trim()
            if (query.isEmpty()) return

            val userMessage = Message(
                role = Constants.ROLE_USER,
                content = query
            )
            val updatedMessages = _messages.value.toMutableList().apply { add(userMessage) }
            _messages.value = updatedMessages

            _newMessage.value = ""

            // 2. Create a StructuredChatCompletionRequest placeholder.
            val request = StructuredChatCompletionRequest(
                model = _currentModel.value?.model.toString(),
                messages = updatedMessages,
                tools = listOf(
                    ToolSpec(
                        type = "function",
                        function = Function(
                            name = "add_spotify_playlist",
                            description = "Adds a playlist from Spotify.",
                            parameters = SchemaConfig.addPlaylistArgsSchema
                        )
                    ),
                    ToolSpec(
                        type = "function",
                        function = Function(
                            name = "add_event",
                            description = "Adds an event to the calendar.",
                            parameters = SchemaConfig.addEventArgsSchema
                        )
                    ),
                    ToolSpec(
                        type = "function",
                        function = Function(
                            name = "get_weather",
                            description = "Retrieves weather info for a given location.",
                            parameters = SchemaConfig.getWeatherArgsSchema
                        )
                    ),
                    ToolSpec(
                        type = "function",
                        function = Function(
                            name = "launch_app",
                            description = "Launches a specified app.",
                            parameters = SchemaConfig.launchAppArgsSchema
                        )
                    )
                )
            )

            // 3. Call the genAI method for structured completion.
            viewModelScope.launch {
                val responseChoice = genAIRepository.structuredCompletion(request)
                responseChoice?.let { choice ->
                    val assistantMessage = choice.message

                    // 4. Check if the assistant is making a function call.
                    if (assistantMessage.content != null) {
                        when (assistantMessage.name) {
                            "add_spotify_playlist" -> {
                                val args = gson.fromJson(assistantMessage.toolChoice?.arguments, AddPlaylistArgs::class.java)
                                spotifyRepository.addPlayList(args.playlistName, args.artist, args.song)
                            }
                            "add_event" -> {
                                val args = gson.fromJson(assistantMessage.toolChoice?.arguments,
                                    AddEventArgs::class.java)
                                calendarRepository.addEvent(args.date, args.time, args.eventName, args.duration)
                            }
                            "get_weather" -> {
                                val args = gson.fromJson(assistantMessage.toolChoice?.arguments,
                                    GetWeatherArgs::class.java)
                                val queriedWeather = weatherRepository.getWeather(args.lat, args.lon)
                            }
                            "launch_app" -> {
                                val args = gson.fromJson(assistantMessage.toolChoice?.arguments,
                                    LaunchAppArgs::class.java)
                                if(args.packageName != "") {
                                    _launchPackage.value = args.packageName
                                }
                            }

                        }
                    } else {
                        assistantMessage.content = ""
                    }
                    // 5. Add the assistant message to the list.
                    val newList = _messages.value.toMutableList().apply { add(assistantMessage) }
                    _messages.value = newList
                }
            }
        } catch (e: Exception) {
            Log.e("ANDY", "An error occurred", e)
        } finally {
            _state.value = _state.value.copy(isBusy = false)
        }
    }

    fun modelChange() {
        // Increment the index and wrap around using modulo operator
        currentModelIndex = (currentModelIndex + 1) % modelList.size
        _currentModel.value = modelList[currentModelIndex]
    }

    fun seekSpotifyTracks(seek: Boolean) {
        // If there's nothing in the list, do nothing
        if (trackDetailsList.isEmpty()) return

        if (seek) {
            // Move to next track (wrap around)
            currentTrackIndex = (currentTrackIndex + 1) % trackDetailsList.size
        } else {
            // Move to previous track (wrap around)
            currentTrackIndex = if (currentTrackIndex - 1 < 0) {
                trackDetailsList.size - 1
            } else {
                currentTrackIndex - 1
            }
        }

        // Update the current track state
        _currentTrackDetails.value = trackDetailsList[currentTrackIndex]
    }

    private fun fetchSocialMedia() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val socials = socialsRepository.getSocials()
                _socialMedia.value = socials
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch social media data", e)
            }
        }
    }

    private fun fetchNextEvent(timeStamp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _nextEvent.value = calendarRepository.getNextEvent(timeStamp)
                _currentDate.value = calendarRepository.getDateComponents(timeStamp)
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch calendar", e)
            }
        }
    }

    private fun fetchLastLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lastLocation = locationRepository.getLatestLocation()
                _lastLocation.value = lastLocation
                lastLocation?.let { location ->
                    // fetch latest weather for last location
                    fetchWeather(location.latitude, location.longitude)
                    fetchNextEvent(location.timestamp.toString())
                    fetchUserProfile(location.location)
                }
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch location data", e)
            }
        }
    }

    private fun fetchSpotifyData(userProfile: UserProfile) {
        viewModelScope.launch(Dispatchers.IO) {
            var spotifyData: Playlist? = null
            try {
                spotifyData = spotifyRepository.getPlayList(aiSuggestedPlaylist)
                trackDetailsList = getTrackDetailsForPlaylist(spotifyData)
                _currentTrackDetails.value = trackDetailsList.first()
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch Spotify data", e)
            }
            _messages.value = listOf<Message>(
                Message(
                    role = Constants.ROLE_SYSTEM,
                    content = PromptHelper.getMasterPrompt(userProfile.toString(), spotifyData.toString())
                )
            )
        }
    }

    private fun fetchUserProfile(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userProfileData = userRepository.getUserRepository()
                _userProfile.value = userProfileData

                userProfileData?.let { profile ->
                    _state.value = _state.value.copy(userName = profile.name)
                    fetchSpotifyData(profile)
                    when (location.lowercase()) {
                        "home" -> _currentStoredLocation.value = StoredLocation(placeName = location, placeCity = profile.location.home.city, placeCountry = profile.location.home.country)
                        "work" -> _currentStoredLocation.value = StoredLocation(placeName = location, placeCity = profile.location.work.city, placeCountry = profile.location.work.country)
                    }
                }
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch user profile data", e)
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherResponse = weatherRepository.getWeather(lat, lon)
                _weather.value = weatherResponse
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch weather data", e)
            }
        }
    }

    private fun fetchCalendarEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val events = calendarRepository.getEventsForDate(dateToday)
                _calendarEvents.value = events
            } catch (e: Exception) {
                Log.e("ANDY", "Failed to fetch calendar events", e)
            }
        }
    }

    suspend fun getTrackDetailsForPlaylist(playlist: Playlist): List<TrackDetails> = coroutineScope {
        // For each track, launch an async coroutine to fetch details
        val deferredDetails = playlist.tracks.map { track ->
            async {
                // Call your existing getTrackDetails method
                spotifyRepository.getTrackDetails(track = track.song, artist = track.artist)
            }
        }

        // Await all async calls and filter out any null results
        deferredDetails.awaitAll().filterNotNull()
    }
}
