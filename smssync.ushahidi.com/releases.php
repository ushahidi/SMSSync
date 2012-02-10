<!DOCTYPE html> 
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SMSSync by Ushahidi</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link href="../css/bootstrap.css" rel="stylesheet" type="text/css"/>
<link href="../css/styles.css" rel="stylesheet" type="text/css" />
<link href="../libs/js/google-code-prettify/prettify.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="../js/fancybox/jquery.fancybox-1.3.4.css" media="screen" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
<script type="text/javascript" src="../js/fancybox/query.mousewheel-3.0.4.pack.js"></script>
<script type="text/javascript" src="../js/fancybox/jquery.fancybox-1.3.4.pack.js"></script>
<script type="text/javascript" src="../js/screenshots.js"></script>
<script src="libs/js/google-code-prettify/prettify.js"></script>
</head>
<body style="padding-top:40px;" onload="prettyPrint()">
<div class="topbar" data-scrollspy="scrollspy" >
    <div class="fill">
        <div class="container">
            <div id="logo">
                <h3>
                    <a href="#">SMSSync</a>
                </h3>
            </div>
            <ul class="nav">
                <li class="active"><a href="/">Home</a></li>
                <li><a href="../releases/">Releases</a></li>
                <li><a href="../download/">Download</a></li>
                <li><a href="../doc/">Documentation</a></li>
                <li><a href="../features/">Features</a></li>
                <li><a href="../screenshots/">Screenshots</a></li>
                <li><a href="http://forums.ushahidi.com/forum/ushahidi-apps">Support</a></li>
            </ul>
        </div>
    </div>
</div>
<div class="container">
    <div class="hero-unit">
        <h2>The free and open source SMS gateway for Android</h2>
    </div>
</div>
<div class="container">
<div class="page-header">
    <h1>Releases History</h1> <small>This page is about release information for SMSSync. Mainly, it documents the version numbers, current stable and development branches. It also states the release dates and where to download the app.</small>
</div>

<h1>Current release</h1>
<ul>
<li>Released December 12, 2011</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.1.9">r12</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.1.9">1.1.9</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Bug:<ul>
<li>Fixed missing title</li>
<li>Reformated sent_timestamp to 13.11.11 14:59 instead of 11-13-11-02:59</li>
<li>Added 5 more languages - French, Japanese, Danish, Russian and Serbian</li>
</ul>
</li>
</ul>
<h2>Development branch:</h2>
<ul>
<li><a href="https://github.com/eyedol/smssync/">master branch</a> open for commit/contributions for SMSSync %release%</li>
</ul>
<h1>Previous releases</h1>

<ul>
<li>Released November 10, 2011</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.9">r10</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.9">1.0.9</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Bug:<ul>
<li>Fixed HTTPS not working on some devices.</li>
<li>Fixed sent_to variable not sending the device's number. </li>
<li>Fixed formatting issue with sent_timestamp.</li>
</ul>
</li>
<li>
<p>Released October 27, 2011</p>
</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.8">r9</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.8">1.0.8</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Feature:<ul>
<li>Added filtering when importing SMS.</li>
<li>Added sent_to variable so the server can receive the phone number the SMS was sent to.</li>
<li>Added message_id variable so the server can receive the unique id of the SMS.</li>
<li>Added full HTTPS support.</li>
</ul>
</li>
<li>
<p>Bug:</p>
<ul>
<li>Fixed issue with app widget not updating  view when pending messages are in the view.</li>
<li>Improved performance with syncing pending messages. </li>
</ul>
</li>
<li>
<p>Released September 25, 2011</p>
</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.7">r8</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.7">1.0.7</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Released September 23, 2011</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.6">r7</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.6">1.0.6</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Feature:<ul>
<li>Added improvement that makes th battery consumption by the app.</li>
<li>Added the ability for enabled services to automatically start after the phone boots up.</li>
<li>Added the ability for the App to lock WiFi connection when the phone is connected to a WiFi.</li>
<li>Added the ability for 'response messages' to be received from the server and not wait on a scheduler.</li>
<li>Added confirmation dialog when performing delete actions.</li>
<li>Added a tab that habours 'pending messages' view and 'sent messages' view.</li>
<li>Added the ability for the app to log recent sent messages. Twenty is the Max.</li>
<li>Added 'sent_timestamp' post variable in order to send the timestamp of an SMS.</li>
<li>Added App widget support.</li>
</ul>
</li>
</ul>
<p>Bug:
    * Fixed a bug that when all messages are deleted, the app shows a failed message yet the messages are deleted.
    * Fixed the issue of pending messages' view not updating when pending messages are deleted from a background service.</p>
<ul>
<li>Released February 25, 2011</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.5">r6</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.5">1.0.5</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Feature:<ul>
<li>Scheduler automatically Syncs pending messages to the configured Callback URL.</li>
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
<p>Released January 31, 2011</p>
</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.4">r5</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.4">1.0.4</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Bug:<ul>
<li>Notification cannot be cleared untill SMSSync is stopped.</li>
<li>Failed Messages now get sent to the outbox folder for later manual syncing when data connection is lost.</li>
</ul>
</li>
<li>
<p>Released January 31, 2011</p>
</li>
<li>Revision <a href="https://github.com/ushahidi/SMSSync/tree/v1.0.4">r5</a></li>
<li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v1.0.4">1.0.3</a></li>
</ul>
<h3>Changelog</h3>
<ul>
<li>Bug:<ul>
<li>Downgraded to Android 1.6 so smssync works from 1.6 and above.</li>
<li>Now sms can be deleted from the sms inbox. This is a configurable option. </li>
<li>Fixed issue with the secret variable.</li>
<li>Better support for localization - All hardcoded strings have been moved to the string.xml file.</li>
<li>Supports outbox -- for pending messages that manually needs to be synced.</li>
<li>Improved SMS background service -- Now it starts and stops perfectly.</li>
<li>Added version number to the powered by text on the Settings screen.</li>
<li>Changed notification Icon to SMSSync's slick launch icon.</li>
<li>Shows status of Pending messages. Whether there are pending messages or not.</li>
<li>Prepopulates URL field with "http://" when setting up the sync URL.</li>
<li>Sends an auto response once the SMS is recieved. This is a configurable option.</li>
<li>Validates the callback URL </li>
</ul>
</li>
</ul>

<p></div>
<div class="container">
<footer class="footer">
    <div class="container">
        <div class="pull-right">
          powered by <a href="http://www.ushahidi.com">Ushahidi</a>
        </div> 
        <p>Generated: 02-02-2012 Copyright &copy; 2010 - 2012 <a href="http://www.ushahidi.com">Ushahidi.com</a></p>
</div>
</footer>
</div>
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
</body>
</html></p>