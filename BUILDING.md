## How To Build SMSSync app

The build setup here supports Android Studio and the new Gradle build system.
To build this project make sure you've either set ANDROID_HOME to point to where
you have your Android SDK installed or you have defined the location of the Android
SDK in the local.property file. In local.property file, make sure `sdk.dir` points to the location of the Android SDK.

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

## How To Build SMSSync website

### Preview website

The SMSSync website hosted on github pages is generated using [ruhoh](http://ruhoh.com).
Please refer to the [ruhoh installation](http://ruhoh.com/docs/2/installation/) guide on how to get it running.

```
$ cd website-src

$ bundle exec ruhoh  server 9292

```

You then preview the site at [http://localhost:9292]( http://localhost:9292)

### Compile HTML files

Compile the ruhoh site to HTML so it can be published to a hosting server. We use github pages to host SMSSync's website.

```
$ cd website-src

$ ruhoh  compile '<path_to_a_folder_to_compile_the_html_into>'

```

**Note:** The folder to compile the html files into has to be empty as the `ruhoh compile` command will delete any files in it.

### Publish compiled HTML files

Publish the compiled HTML files to [github pages](http://ushahidi.github.io/SMSSync/)

```
$ cd <path_compiled_html_folder>

$ git init .

$ git add .

$ git commit -m "<update_message>"

$ git push git@github.com:ushahidi/SMSSync.git master:gh-pages --force

```