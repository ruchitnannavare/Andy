# Andy
Overview
Andy is an Android application developed in Kotlin as part of a technical assessment. This README provides details about the application, including its purpose, design decisions, development process, and features.

What is Andy? 
Andy is an everything assistant built with the purpose of decluttering the modern smartphone experience and reimagining it backed by GenAI. Andy supports native function calling to convert simple text requests to actions while also handling general queries as it is backed by capable models, in this case by GPT-4o and O3-mini.

First boot
Copy the file named CopyApiKeys.kt in the utils folder and refactor its name to ApiKeys.kt. Andy's prototype requires an OpenAI developer token for LLM remote hosting with Spotify developer Client ID and Secret keys for accessing Spotify APIs.

Andy's design
In my view, an everything app is something the user shall have open all the time, and thus its design needs to be very personal and customizable. With Dan being a software engineer with an agenda to build a minimalist UI/UX, this flavor of Andy is for Dan. It has a retro-modern design language with terminal-like fonts and pastel gradients. Andy is a single-activity, no-bloat application that contains two layers, widget layer and chat layer, accessed by combinations of multiple StateFlows. The widget layer is the greeting view for the user that can contain notification widgets (calendar and socials) and recommendation widgets (Spotify playlist recommendation).

Assumptions:
1) Andy heavily uses internet connectivity and assumption is going to be connected with Internet thorughout runtime.
2) Andy being a very personalized app, I have assumed Dan is gravitated more towards green for Andy's thematic experience. A production quality version of Andy should have different themes for fonts and colors and even logo color personalization as well.
3) Because the developer is also the intended first user, UI controls shall also be less obvious, giving flexibility for radical UI components that serve multiple purposes.
4) I have also assumed Dan is someone profoundly intrigued by GenAI and its applications and has a functioning idea of how models work internally and in-depth knowledge about how different models differ from each other with their own upsides and shortcomings and hhence model selector has two pre
5) The current message template is also set as a preference; Dan has a terminal-like interface, but other users can have a chat bubble experience as well.
6) The Spotify widget is assumed to be powered by a custom Spotify agent that shows generated playlists based on the user's music taste and is injected through SpotifyRepository and currently its working on assumption that the selected playlist is "Evening chills".
7) The Calendar widget is assumed to get schedules from the user's calendar app.
8) The Weather widget shows weather information for the user's last location from location service which gets the last location from the 
9) The Twitter widget shows the latest post from X APIs, which is injected through socials service that would contain APIs for other social media platforms.
10) GenAI capability is assumed as a self-hosted remote LLM service with access to open-source models like Mistral and DeepSeek.
These widgets should also be editable according to user preferences in a more refined version.

Guide:
Widget Screen:
Clicking the weather widget launches intent for the weather app.
Clicking "+" icon on calendar launches intent to the calendar app.
Clicking X widget launches intent for the X app
The Spotify widget has custom-made next, previous, and play icons to browse suggested playlists, and play launches the song on the Spotify music app.

Chat Screen:
Focusing on Andy entry makes Chat layer visible. Chat layer contains only handful design elemnts to keep design as decluttered as possible.
1) The Chat layer's top contains the Andy app icon which also clears messages on double-tapping it.
2) Next to the Andy icon shows the language model around which the Andy assistant is wrapped. Dragging the model name to the right and letting go switches to a different model.
3) Andy's goal is to provide user preference over base model or agent to have a more refined querying experience. Andy's multi-model approach allows Dan to choose from GPT-4o for short quick queries and O3-mini for long reasoning queries.
4) The send button contains a custom-designed icon showing a minimalist plus icon. It is supposed to rotate while Andy thinks, pause when a response arrives, and pick up rotation from the same angle for the next query to give the user a sense of continuity, but I haven't been able to figure out why it doesn't yet.
5) Andy contains native function calling support for getting weather of cities, adding an event to calendar, adding a track to a Spotify playlist, or creating a new playlist itself.

Current function calling support:
1) Ask Andy to add a track with artist name in your playlists
2) Ask Andy about weather for any city around the globe and it will show precise weather forecast for next hour
3) As Andy to add an event in the caendar and it will add the event after confirming eventname, date time and duration
4) Ask Andy to launch an intent and it will launch the external intent if the package name is registered in the manifest.

Development: 
Andy being my my first Kotlin native android project, these large language models were crucial tools to churn out function chunks of code based on my understanding of the app.
1) GPT-4.5 with research preview - This model helped me map my existing .NET MAUI C# business logic knowledge to develop Andy in Kotlin. I used Jetpack Compose to build UI/UX and kept logic in a separate view model layer through Hilt dependency injection.
2) GPT-3.5-mini-high - Once having a good understanding of what libraries to use and how the application should be shaped, this model helped me generate chunks of logic implementing features module by module.
3) Claude-Sonnet and GPT-4o - To handle code refactoring, generating output tailor-made for my usage for slightly deviated solutions from bigger models, doing this kept the context of load-bearing models clear and got better bandwidth.
4) Spotify, Calendar, Location, Weather, User profile data, and Social data are all dependency-injected in the view model. Spotify playlists, Calendar, Socials, and user data implementations are set to mimic APIs but read and write data into the copied version of original JSON and CSV files in Application Files directory. Original raw data is in res/raw and is only read once during first bootup.
5) Weather, Spotify track details, and OpenAI APIs are injected through a custom API client based on the OkHttp3 package.
6) These injected service functions are accessed at runtime during bootup and function calling from Andy's chat interface.

Learning a new UI language was a bit tricky; for this, I sketched out wireframes of UI/UX elements which allowed me to have rapid iteration of different design ideas and understand Jetpack Compose's functional UI language. These are some wireframes from development.
![IMG_6865](https://github.com/user-attachments/assets/7791d696-c43b-48eb-8bd7-ec459f65ed4b)
![IMG_6866](https://github.com/user-attachments/assets/e214b023-b0af-4355-9bcf-354e02f0874b)
![IMG_6867](https://github.com/user-attachments/assets/b179d43b-63a9-487b-85fa-134f05e42b70)
![IMG_6894](https://github.com/user-attachments/assets/04a2b030-7f8d-42e7-a99c-0c4cbc419086)

Open issues:
1) Main scaffold doesn't compress as it should while soft keyboard appears
2) Keyboard overlaps the new message entyry control
3) Send button animation doesn't play while waiting for the response.

