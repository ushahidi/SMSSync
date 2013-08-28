The build setup here supports Android Studio and the new Gradle build system.
To build this project make sure you've either set ANDROID_HOME to point to where
you have your Android SDK installed or you have defined the location of the Android
SDK in the local.property file. In local.property file, make sure `sdk.dir` equals
 to the location of the Android SDK.

Step 1:

	Create a local.properties file in the root document of the project and insert the absolute path where you have
	your sdk stored. Eg. `sdk.dir=/home/username/android-sdk-linux_x86`

Step 2: 
	Build the app on the command line `./build.sh` 
	
	OR
	
	Build with Android Studio.
		* Go to File
		* Select Import Project...
		* Select `build.gradle` located in the project's root document.
		* After it finishes the import, hit Shift + F10 to build the project.

