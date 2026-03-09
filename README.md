An Android application demonstrating background video recording using CameraX and a Foreground Service.
The app provides live camera preview, allows users to start/stop recording, and saves recorded videos directly to the device gallery.

This project is useful for developers who want to learn:

CameraX video recording

Foreground services

Background camera usage

Saving media using MediaStore API

✨ Features

📷 Camera preview using CameraX

🎥 Record video with audio

🔄 Background recording using Foreground Service

🔔 Persistent notification during recording

💾 Save recorded video to device gallery

🔐 Runtime permission handling

⚡ Simple and clean architecture

🏗 Architecture

The application uses a Service-based architecture where camera operations run in a background service.

MainActivity
     │
     ▼
VideoRecordingService
     │
     ├── CameraX Preview
     ├── VideoCapture
     ├── Recording Controller
     └── Save to MediaStore
Components

MainActivity

Handles UI interactions

Requests permissions

Starts and binds the service

Displays camera preview

VideoRecordingService

Runs as a Foreground Service

Manages CameraX lifecycle

Starts and stops recording

Saves videos to gallery

🛠 Tech Stack
Technology	Purpose
Kotlin	Main programming language
CameraX	Camera and video recording
Foreground Service	Background recording
MediaStore API	Saving videos to gallery
ViewBinding	UI binding
AndroidX Libraries	Core Android components
📸 Screenshots

Add screenshots here after uploading them to your repository.

Example:

screenshots/
   preview_screen.png
   recording_screen.png

Then display them like this:

![Preview](screenshots/preview_screen.png)
![Recording](screenshots/recording_screen.png)
▶️ Running the Project
1️⃣ Clone the repository
git clone https://github.com/ankitsharma2214/BackgroundVideoRecordingApp.git
2️⃣ Open in Android Studio

Open the project in Android Studio Hedgehog or newer.

3️⃣ Connect a device

Run the app on a real Android device because camera recording may not work properly on emulators.

4️⃣ Run the application

Click Run ▶ in Android Studio.

📂 Output Location

Recorded videos are saved in:

Movies/MyVideos/

inside the device gallery.

🔐 Permissions Used

The app requires the following permissions:

CAMERA
RECORD_AUDIO
FOREGROUND_SERVICE
FOREGROUND_SERVICE_CAMERA
FOREGROUND_SERVICE_MICROPHONE
🚀 Possible Improvements

Future enhancements could include:

Front / back camera switching

Video quality selection

Background recording timer

Video compression

Upload recorded videos to server

Recording status indicator in UI

👨‍💻 Author

Ankit Sharma

Android Developer

GitHub:
https://github.com/ankitsharma2214

⭐ If you find this project helpful, please star the repository.
