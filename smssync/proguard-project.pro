# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

## --------------- Start Project specifics --------------- ##

# Keep the BuildConfig
-keep class org.addhen.smssync.BuildConfig { *; }

# Keep the support library, but:
# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see https://code.google.com/p/android/issues/detail?id=78377
# and https://stackoverflow.com/questions/24809580/noclassdeffounderror-android-support-v7-internal-view-menu-menubuilder
-keep class !android.support.v7.internal.view.menu.**,android.support.** {*;}

# Application classes that will be serialized/deserialized over Gson
# or have been blown up by ProGuard in the past
-keep class org.addhen.smssync.models.** {*;}

-keep class org.addhen.smssync.util.** {*;}
-dontwarn org.addhen.smssync.util.**

## ---------------- End Project specifics ---------------- ##
