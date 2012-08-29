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
<link href='//fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic' rel='stylesheet' type='text/css'>
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
                    <li class="active"><a href="howto">How To</a></li>
                    <li><a href="doc">Documentation</a></li>
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
    <h1>How To<small> This page documents how to setup SMSSync on your Android device.</small></h1>
</div>

<h3>Overview</h3>
<p>This how to covers the essential configuration to get SMSSync up and running. The application has been designed to be intuitive enough to easily get it running. </p>
<p><strong>Note</strong> SMSSync runs on any device with Android 2.2 and above. However because it relies heavily on SMS, the device needs to support SMS.</p>
<h2>Manage Sync URL</h2>
<p>Here you can manage multiple Sync URLs. Sync URLs are the URLs of your webservices. These are where the messages from SMSSync are actually pushed to.
<div class="row-fluid">
<div class="span6"></p>
<h4>Add new Sync URL</h4>
<p>To add a new Sync URL:</p>
<ol>
<li>Tap on the <strong><em>Sync URL</em></strong> tab.</li>
<li>Tap on the <img alt="Add icon" src="images/icons/new.png" /> icon on the actionbar. An input dialog should open.</li>
<li>Enter a title for the Sync URL.</li>
<li>Enter an optional secret key. If there is secret key on the webservices. Make sure you enter the exact key here.</li>
<li>Enter a comma separated value for the keyword(s). These keywords will be used by SMSSync to filter incoming SMS and pending messages to the Sync URL you are adding.</li>
<li>Enter the URL for your webservice. Don't forget to start with the <strong><em>HTTP</em></strong> or <strong><em>HTTPS</em></strong> protocol.</li>
<li>Tap <strong><em>Ok</em></strong> to save the new entry or <strong><em>Cancel</em></strong> to cancel adding the new entry.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Edit new Sync URL</h4>
<p>To edit an existing Sync URL:</p>
<ol>
<li>Long press on the Sync URL you want to edit by tapping and holding it untill the actionbar menu changes.</li>
<li>Tap on the <img alt="Edit icon" src="images/icons/edit.png" /> icon on the actionbar. An input dialog should opened repopulated with the details of the Sync URL.</li>
<li>Edit the necessary field(s) accordinlgy.</li>
<li>Tap <strong><em>Ok</em></strong> to save the edited entry or <strong><em>Cancel</em></strong> to cancel editing the existing entry.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Delete All Existing Sync URL</h4>
<p>Delete all Sync URL by:</p>
<ol>
<li>If there are any Sync URL enabled(checked), disable(uncheck) them. Otherwise, move to the next item.</li>
<li>Stop the SMSSync service if it's running(checked). Otherwise, move to the next point.</li>
<li>Tap on the <img alt="Delete icon" src="images/icons/delete.png" /> icon on the actionbar. A confirmation dialog should opened.</li>
<li>Tap <strong><em>Yes</em></strong> to delete all Sync URL or <strong><em>No</em></strong> to cancel the deletion.</li>
</ol>
<p></div>
</div>
<div class="row-fluid">
<div class="span6"></p>
<h4>Delete An Existing Sync URL</h4>
<p>Delete an existing Sync URL by:</p>
<ol>
<li>If the Sync URL to be deleted is enabled(checked), disable it. Otherwise, move to the next item.</li>
<li>Long press on the Sync URL you want to delete by tapping and holding it untill the actionbar menu changes.</li>
<li>Tap on the <img alt="Delete icon" src="images/icons/delete.png" /> icon on the actionbar. A confirmation dialog should opened.</li>
<li>Tap <strong><em>Yes</em></strong> to delete it or <strong><em>No</em></strong> to cancel the deletion.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Start SMSSync service</h4>
<p>To start the SMSSync service:</p>
<ol>
<li>Make sure you have added and enabled(checked) at least one Sync URL.</li>
<li>On the <strong><em>SYNC URL</em></strong> tab, tap on the <strong><em>Start SMSSync service</em></strong> to start the service. You do this if the service is disabled.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Stop SMSSync service</h4>
<p>To stop the SMSSync service:</p>
<ol>
<li>On the <strong><em>SYNC URL</em></strong> tab, tap on the <strong><em>Start SMSSync service</em></strong> to stop the service. You do this if the service is enabled.</li>
</ol>
<p></div>
</div>
<div class="row-fluid">
<div class="span12"></p>
<h3>Configure</h3>
<p>With the options available on the app, you can configure it to behave to your preferences.
</div>
</div>
<div class="row-fluid">
<div class="span6"></p>
<h4>General Settings</h4>
<ol>
<li>Go to Settings screen by tapping the <img alt="Settings icon" src="images/icons/settings.png" /> icon on the actionbar or option menu.</li>
<li>Tap on <strong><em>Enter Unique ID</em></strong> to enter a Unique ID to identify the device SMSSync is installed on.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Reply Message Settings</h4>
<p>This is where you set auto reply message to be sent to the user that sent SMS to SMSSync. You can specify a message on the phone or get the message from the server.
To set a reply message:</p>
<ol>
<li>Go to Settings screen by tapping the <img alt="Settings icon" src="images/icons/settings.png" /> icon on the actionbar or option menu.</li>
<li>Tap on <strong><em>Get Reply From Server</em></strong> if you want to send an instant reply message from the Sync URL. Otherwise, tap on <strong><em>Reply Messages</em></strong> to set the instant reply message on the phone.</li>
</ol>
<p></div>
</div>
<div class="row-fluid">
<div class="span6"></p>
<h4>Auto Sync Settings</h4>
<p>This is where you enable SMSSync to periodically send pending messages to the enabled Sync URLs.
To enable auto sync:</p>
<ol>
<li>Go to Settings screen by tapping the <img alt="Settings icon" src="images/icons/settings.png" /> icon on the actionbar or option menu.</li>
<li>Tap on <strong><em>Enable Auto Sync</em></strong> to enable the auto sync service.</li>
<li>Tap on <strong><em>Auto sync frequency</em></strong> to set the frequency at which the auto sync should run.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Task Settings</h4>
<p>The Task feature allows you to send SMS to users. All the messages to be sent to the users are configured on the server.
To enable Task Checking feature:</p>
<ol>
<li>Go to Settings screen by tapping the <img alt="Settings icon" src="images/icons/settings.png" /> icon on the actionbar or option menu.</li>
<li>Tap on <strong><em>Enable Task Checking</em></strong> to enable the task service.</li>
<li>Tap on <strong><em>Frequency</em></strong> to set the frequency at which the task checking service should run.</li>
</ol>
<p></div>
</div>
<div class="row-fluid">
<div class="span12"></p>
<h3>Manage Pending Messages</h3>
<p>Pending messages are failed SMS that couldn't make it to any of the enabled Sync URLs. SMSSync allows you to manage these pending messages. You can either configure the app to automatically or manually sync these failed messages or delete them. This section of the how to is going to show you how to manage pending messages.
</div>
</div>
<div class="row-fluid">
<div class="span6"></p>
<h4>Manaully sync all pending messages</h4>
<p>Manully sync all pending messages by:</p>
<ol>
<li>Tap on the <img alt="Sync icon" src="images/icons/refresh.png" /> icon on the actionbar. The sync icon should spin for a while.</li>
<li>After the sync icon spins for a while, a Toast should show up with a status message.
</div>
<div class="span6"></li>
</ol>
<h4>Manaully sync a pending message</h4>
<p>Manually sync a pending message by:</p>
<ol>
<li>Long press on the pending message you want to sync by tapping and holding it untill the actionbar menu changes.</li>
<li>Tap on the <img alt="Sync icon" src="images/icons/refresh.png" /> icon on the actionbar. The sync icon should spin for a while.</li>
<li>After the sync icon spins for a while, a Toast should show with a status message.</li>
</ol>
<p></div>
<div class="row-fluid">
<div class="span6"></p>
<h4>Delete pending messages</h4>
<p>Delete all pending messages saved on the device.</p>
<ol>
<li>Tap on the <img alt="Delete icon" src="images/icons/delete.png" /> icon on the actionbar. A confirmation dialog should opened.</li>
<li>Tap <strong><em>Yes</em></strong> to delete all pending messages or <strong><em>No</em></strong> to cancel the deletion.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Import SMS</h4>
<p>Import SMS from the device's messages inbox.</p>
<ol>
<li>Tap on the <img alt="Import icon" src="images/icons/import.png" /> icon on the actionbar. A progress dialog should opened. Leave it to run until it finishes. Depending on the messages in your inbox, this should take a while.</li>
</ol>
<p></div>
</div>
<div class="row-fluid">
<div class="span12"></p>
<h3>Manage Sent Messages</h3>
<p>Sent messages are SMS or pending messages that have successfully been sync'd to the enabledSync URL. This is to allow you to see what messages were successfully sent. There is nothing much you can do here. You can only view and delete sent messages. This section will show you how to delete sent messages individually or all at the same time.
</div>
</div>
<div class="row-fluid">
<div class="span6"></p>
<h4>Delete a sent message</h4>
<p>Delete an individual message by:</p>
<ol>
<li>Long press on the sent message you want to delete by tapping and holding it until the actionbar menu changes.</li>
<li>Tap on the <img alt="Delete icon" src="images/icons/delete.png" /> icon on the actionbar. A confirmation dialog should opened.</li>
<li>Tap <strong><em>Yes</em></strong> to delete it or <strong><em>No</em></strong> to cancel the deletion.</li>
</ol>
<p></div>
<div class="span6"></p>
<h4>Delete all sent messages</h4>
<p>Delete all saved sent messages by:</p>
<ol>
<li>Tap on the <img alt="Delete icon" src="images/icons/delete.png" /> icon on the actionbar. A confirmation dialog should opened.</li>
<li>Tap <strong><em>Yes</em></strong> to delete all sent messages or <strong><em>No</em></strong> to cancel the deletion.</li>
</ol>
<p></div>
</div>
</div></p></div>

<footer class="footer">
    <div class="container">
        <div class="pull-right">
          powered by <a href="http://www.ushahidi.com">Ushahidi</a>
        </div> 
        <p>Generated: 29-08-2012 Copyright &copy; 2010 - 2012 <a href="http://www.ushahidi.com">Ushahidi.com</a></p>
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