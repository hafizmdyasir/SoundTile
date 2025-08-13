# Sound Tile
> By Mohammad Yasir

**Sound Tile** is a minimal, lightweight, Kotlin-based application for Android version 15 and above that adds a quick settings toggle to switch between ringer and vibrate modes. The toggle requires `android.permission.ACCESS_NOTIFICATION_POLICY` permission to be granted and it is requested the first time the user taps on the tile. Note that this permission is required to switch from DND (Silent) mode to any other mode.

### Installation
A signed APK is provided as a release with this repository. You are welcome to build your own release should you so desire.

### Building
Cloning this repository should enable you to open it in Android Studio. If not, the only file that contains anything useful is the `Tiling.kt` service, and you can browse it directly to understand the logic behind it.

