<!DOCTYPE html> 
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SMSSync by Ushahidi</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="SMSSync the free and open source SMS gateway for Android">
<meta name="author" content="Ushahidi Inc.">
<link href="libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
<link href="css/sunburst.css" rel="stylesheet"/>
<link href='http://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Racing+Sans+One' rel='stylesheet' type='text/css'>
<link rel="stylesheet" type="text/css" href="js/fancybox/jquery.fancybox-1.3.4.css" media="screen" />
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->

<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->

<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>

<script type="text/javascript" src="js/fancybox/jquery.mousewheel-3.0.4.pack.js"></script>

<script type="text/javascript" src="js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>

<script type="text/javascript" src="js/screenshots.js"></script>

<script src="libs/js/google-code-prettify/prettify.js"></script>

<script src="libs/bootstrap/js/bootstrap.min.js"></script>

<p></head>
<body style="padding-top:40px;" data-spy="scroll" onload="prettyPrint()"></p>
<!-- nav bar -->

<div class="navbar navbar-inverse navbar-fixed-top" data-spy="scroll" >
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".subnav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <div id="logo">
                <a class="brand" href="index">SMSSync</a>
            </div>
            <div class="nav-collapse subnav-collapse">
                <ul class="nav">
                    <li><a href="index">Home</a></li>
                    <li><a href="releases">Releases</a></li>
                    <li><a href="download">Download</a></li>
                    <li><a href="howto">Configure</a></li>
                    <li><a href="doc">Developers</a></li>
                    <li><a href="features">Features</a></li>
                    <li><a href="screenshots">Screenshots</a></li>
                    <li><a href="http://forums.ushahidi.com/forum/ushahidi-apps">Support</a></li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- nav bar ends -->

<!-- header starts -->

<div class="container">
    <div class="hero-unit">
        <h2>The free and open source SMS gateway for Android</h2>
    </div>
</div>

<!-- header ends -->

<!-- body starts -->

<div class="container">
<div class="page-header">
    <h1>Releases History <small>This page is about release information for SMSSync. It provides changelogs and version numbers of old releases, and details of current stable and development branches.</small></h1>
</div>

<h1>Current Release</h1>
<ul>
<li>Released March 20<sup>th</sup>, 2013</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v2.0.2">v2.0.2</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Feature:</li>
<li>Show confirmation prompt when attempting to import SMS</li>
<li>Added RegExp support to keyword filtering.</li>
<li>Added more useful HTTP and server error messages</li>
<li>Simplified the actionbar menu. Only show the relevant menus</li>
<li>Updated notification code to make use the notification.compact library</li>
<li>
<p>Add Urkainian language</p>
</li>
<li>
<p>Bug:</p>
</li>
<li>Fix wifi lock issue <a href="https://github.com/ushahidi/SMSSync/issues/56">#56</a></li>
<li>Improved wording and typos in the strings file</li>
<li>Disable excessive "no task enabled" notifications</li>
<li>Upgrade abs library to 4.2.0</li>
<li>Fix ID issue <a href="https://github.com/ushahidi/SMSSync/issues/60">#60</a>. Now uses UUID instead</li>
<li>Fix issue with auto sync failing</li>
</ul>
<h1>Previous releases</h1>
<ul>
<li>Released October 22<sup>nd</sup>, 2012</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v2.0.1">v2.0.1</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Feature:</p>
<ul>
<li>Resizable widgets to fit on most screens.</li>
<li>Upgraded ABS library to 4.2.2.</li>
<li>Added Korean translation.</li>
</ul>
</li>
<li>
<p>Bug:</p>
<ul>
<li>Fixed an issue with pending messages. See issue <a href="https://github.com/ushahidi/SMSSync/issues/44">#44</a></li>
<li>Removed screen restriction support from manifest file. This is to allow it to show up on the Google play store for most devices.</li>
</ul>
</li>
<li>
<p>Released September 3<sup>rd</sup>, 2012</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v2.0.0">v2.0.0</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Feature:</p>
<ul>
<li>Improved the UI. Optimized it to scale well on tablet devices.</li>
<li>Added the ability to manage multiple Sync URLs and the ability to sync messages to them.</li>
<li>Added icons to actionbar menu instead of just text.</li>
<li>Cleaned up code based making it more modular and easier to maintain.</li>
<li>Increased the frequency options for the various schedulers. Added 1 minute, 2 minutes, 3 minutes and 4 minutes.</li>
<li>Dropped support for devices running 1.6. This is to allow us to take advantage of the new Android APIs.</li>
</ul>
</li>
<li>
<p>Bug:</p>
<ul>
<li>Fixed task checking issue. It now frequently pings client for tasks.</li>
<li>sent_timestamp variable  now sends the raw timestamp instead of the preformatted one. This is to allow the client to have control of the formatting.</li>
<li>Removed the characters constraints to Unique ID field.</li>
</ul>
</li>
<li>
<p>Released December 12<sup>th</sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.1.9">v1.1.9</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Bug:</p>
<ul>
<li>Fixed missing title</li>
<li>Reformatted sent_timestamp to <code>13.11.11 14:59</code> instead of <code>11-13-11-02:59</code></li>
<li>Added 5 more languages - French, Japanese, Danish, Russian and Serbian</li>
</ul>
</li>
<li>
<p>Released November 10<sup>th</sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.9">v1.0.9</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Bug:</p>
<ul>
<li>Fixed HTTPS not working on some devices.</li>
<li>Fixed sent_to variable not sending the device's phone number. </li>
<li>Fixed formatting issue with sent_timestamp.</li>
</ul>
</li>
<li>
<p>Released October 27<sup>th</sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.8">v1.0.8</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Feature:</p>
<ul>
<li>Added filtering when importing SMS.</li>
<li>Added sent_to variable so the server can receive the phone number the SMS was sent to.</li>
<li>Added message_id variable so the server can receive the unique id of the SMS.</li>
<li>Added full HTTPS support.</li>
</ul>
</li>
<li>
<p>Bug:</p>
<ul>
<li>Fixed issue with app widget not refreshing when pending messages are in the view.</li>
<li>Improved performance with syncing pending messages. </li>
</ul>
</li>
<li>
<p>Released September 25<sup>th</sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.7">v1.0.7</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Released September 23<sup>rd</sup>, 2011</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.6">v1.0.6</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Feature:<ul>
<li>Added improvement to the app's battery consumption.</li>
<li>Added the ability for enabled services to automatically start after the phone boots up.</li>
<li>Added the ability for the App to lock WiFi connection.</li>
<li>Added the ability for 'response messages' to be received from the server and not wait on a scheduler.</li>
<li>Added confirmation dialog when performing delete actions.</li>
<li>Added a tab that harbours 'pending messages' view and 'sent messages' view.</li>
<li>Added the ability for the app to log recent sent messages. Twenty is the Max.</li>
<li>Added 'sent_timestamp' post variable in order to send the timestamp of an SMS.</li>
<li>Added App widget support.</li>
</ul>
</li>
</ul>
<p>Bug:
    * Fixed a bug that when all messages are deleted, the app shows a failure message yet the messages are deleted.
    * Fixed the issue of 'pending messages' view not updating after pending messages are deleted by a background service.</p>
<ul>
<li>Released February 25<sup>th</sup>, 2011</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.5">v1.0.5</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Feature:</p>
<ul>
<li>Scheduler automatically syncs pending messages to the configured Callback URL.</li>
<li>Now sends out messages from the configured callback URL as text message.</li>
<li>Imports messages from Android messages app inbox for synchronizing to the configured callback URL.</li>
<li>Added context menu for deleting or synchronizing pending messages. <br />
</li>
</ul>
</li>
<li>
<p>Bug:</p>
<ul>
<li>Improved callback URL validation process; Added more checks. Empty, malformed and connection checks.</li>
<li>More sanity checks before SMSSync can be enabled. </li>
</ul>
</li>
<li>
<p>Known issue:</p>
<ul>
<li>Scheduler doesn't refresh pending messages' list after it runs. The pending messages screen needs to be restarted. </li>
</ul>
</li>
<li>
<p>Released January 31<sup>st</sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.4">v1.0.4</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>
<p>Bug:</p>
<ul>
<li>Notification cannot be cleared until SMSSync is stopped.</li>
<li>Failed Messages now get sent to the outbox folder for later manual syncing when data connection is lost.</li>
</ul>
</li>
<li>
<p>Released January 31<sup>st<sup>, 2011</p>
</li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.4">v1.0.3</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Bug:<ul>
<li>Downgraded to Android 1.6 so SMSSync works from 1.6 and above.</li>
<li>Now SMS can be deleted from the SMS inbox. This is a configurable option. </li>
<li>Fixed issue with the secret variable.</li>
<li>Better support for localization - All hardcoded strings have been moved to the string.xml file.</li>
<li>Supports outbox for pending messages that need to be synced manually.</li>
<li>Improved SMS background service - now it starts and stops perfectly.</li>
<li>Added version number to the powered by text on the Settings screen.</li>
<li>Changed notification Icon to SMSSync's slick launch icon.</li>
<li>Shows status of Pending messages. Whether there are pending messages or not.</li>
<li>Prepopulates URL field with "http://" when setting up the sync URL.</li>
<li>Sends an auto response once the SMS is recieved. This is a configurable option.</li>
<li>Validates the callback URL.</li>
</ul>
</li>
</ul></div>

<footer class="footer">
    <div class="container">
        <div class="row-fluid">
            <div class="span4">
                Generated: 19-03-2013
            </div>
            <div class="span4">
                Copyright &copy; 2010 - 2012 <a href="http://www.ushahidi.com">Ushahidi.com</a>
            </div>
            <div class="span4">
                <p class="pull-right">
                    <span class="smssync">SMSSync</span><span>&nbsp;</span>  powered by <a href="http://www.ushahidi.com">Ushahidi</a>
                </p>
            </div>
        </div>  
    </div>
</footer>

<script type="text/javascript">
var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-12063676-22']);
_gaq.push(['_trackPageview']);
(function() {
var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();
</script>

<p></body>
</html></p>