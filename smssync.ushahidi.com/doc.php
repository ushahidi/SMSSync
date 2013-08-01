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
                    <li><a href="https://wiki.ushahidi.com/display/forums/Ushahidi+Forums">Support</a></li>
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
<h1>Developers Documentation <small>This shows how to integrate SMSSync into your project.</small></h1>
</div>

<div class="row-fluid">
<div class="span6">
<h2>Instructions</h2>
<ul>
<li>
SMSSync uses the <a href="http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol">HTTP</a> and <a href="http://en.wikipedia.org/wiki/HTTPS">HTTPS</a> protocols for communication.<br />
To start the SMSSync Gateway, you'll need to specify a Sync URL. This URL is where all incoming SMS will be transmitted to. Remember to enter the full URL including the filename. A typical example will be <code>http://somedomain.com/index.php</code>
</li>
<li>
For security you can specify a secret key to be sent to the Sync URL. If the secret key doesn't match on the server, the Sync URL can ignore the transmission.
</li>
<li>
Additionally, you can specify keywords with which to filter incoming SMS. Only matching messages will be forwarded to the SMSSync Gateway URL.
</li>
<li>
SMSSync uses the following variables to transmit the incoming SMS via the POST method:
<ul>
<li><strong>from</strong> -- the number that sent the SMS</li>
<li><strong>message</strong> -- the SMS sent</strong></li>
<li><strong>message_id</strong> -- the unique ID of the SMS</li>
<li><strong>sent_to</strong> -- the phone number the SMS was sent to</li>

<li><strong>secret</strong> -- the secret key set on the app</li>
<li><strong>sent_timestamp</strong> -- the timestamp the SMS was sent. In the UNIX timestamp format</strong></li>
</ul>
</li>
</ul>
<p>
In order for SMSSync to ensure perfect transmission, the Sync URL must return a JSON-formatted status message, as shown below.
<br /><br />
<strong>Succeeded</strong> 
<pre class="prettyprint linenums">
{
    payload: {
        success: "true"
    }
}</pre>
<br /><br />
<strong>Failed</strong>
<br />
<pre class="prettyprint linenums">{
    payload: {
        success: "false"
    }
}</pre>
</p>
<p>&nbsp;</p>
<strong>Response from server</strong>
<p>
SMSSync allows either an auto-response message to be configured on the app itself, or to be retrieved from the server. When the app makes an HTTP Post request to sync the incoming SMS to the configured URL, the server can respond with JSON-encoded messages alongside the success message. The app then sends these messages by SMS to the specified users phone.
</p>
<p>
This makes it possible to have an instant response via SMS when an HTTP Post request is made. To leverage this feature, a JSON formatted string like the one below needs to be returned by the configured URL in response to the app's HTTP Post request. 
</p>
<p>
In the app itself, ensure <strong>*Get Reply from Server*</strong> is checked to enable this feature.
<p>
<strong>Response JSON data from the Sync URL</strong>
<pre id="code" class="prettyprint linenums">{
    "payload": {
        "success": "true",
        "task": "send",
        "messages": [
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            }
        ]
    }
}</pre>
</p>
<p>&nbsp;</p>
<strong>Task</strong>
<p>
SMSSync supports execution of tasks defined on the server. Currently it supports sending of messages sent from the Sync URL as SMS. This feature is targeted towards developers. The app can be configured to poll the server for new tasks at a given frequency. The server then needs to respond to HTTP GET requests with <code>?task=send</code> (for example <code>http://callback_url/smssync?task=send&secret=secret_key</code>). The format of this response is shown below.
</p>
<br /><br />        

<p><strong>Response JSON data from the Sync URL</strong></p>

<pre class="prettyprint linenums">{
    "payload": {
        "task": "send",
        "secret": "secret_key",
        "messages": [
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here" 
            }
        ]
    }
}</pre>

<br /><br />

<strong>Notes:</strong>
<ul>
<li>
The secret key provided by the server must match the secret key configured within SMSSync, otherwise SMSSync will not execute the task.
</li>
<li>
To ensure the message is sent to the correct recipient, add the country code to the phone number. Eg. <strong>+254</strong>700709142. Without this, the message is sent to the number in the country where the phone is.
</li>
<li>
The web-service should check the value of the secret passed with each task http request that smsync makes to the web-service for messages to send and respond appropriately so that not just any running smssync instance can communicate with your web-service.
</li>
</ul>

</div>

<div class="span6">
<h2>A sample web service</h2>
<p>This is a sample PHP script to demonstrate how to write a webservice to successfully communicate with SMSSync.</p>
<pre class="prettyprint linenums">
// Get the phone number that sent the SMS.
if (isset($_POST['from'])) {
    $from = $_POST['from'];
}

// Get the SMS aka the message sent.
if (isset($_POST['message'])) {
    $message = $_POST['message'];
}

// Get secret key to validate later.
if (isset($_POST['secret'])) {
    $secret = $_POST['secret'];
}

// Get the timestamp of the SMS
if(isset($_POST['sent_timestamp'])) {
    $sent_timestamp = $_POST['sent_timestamp'];
}

// Get phone number of the SMSSync device
if (isset($_POST['sent_to'])) {
    $sent_to = $_POST['sent_to'];
}

// Get the unique message id
if (isset($_POST['message_id'])) {
    $message_id = $_POST['message_id'];
}

// Set success to false as the default success status
$success = "false";

/**
 * Now we have retrieved the data sent by SMSSync 
 * via HTTP. Next, we handle this data. In this demo,
 * we simply save the data to a text file.
 */
if ((strlen($from) > 0) AND (strlen($message) > 0) AND 
    (strlen($sent_timestamp) > 0 ) AND (strlen($sent_to) > 0) 
    AND (strlen($message_id) > 0)) {

    /**
     * The secret key set here is 123456.
     * Make sure this is set in SMSSync. 
     */
    if ( ( $secret == '123456')) {
        $success = "true";
    }

    // Now write the info to a file called test.txt
    $string = "From: ".$from."\n";
    $string .= "Message: ".$message."\n";
    $string .= "Timestamp: ".$sent_timestamp."\n";
    $string .= "Messages Id:" .$message_id."\n";
    $string .= "Sent to: ".$sent_to."\n\n\n";
    $myFile = "test.txt";
    $fh = fopen($myFile, 'a') or die("can't open file");
    @fwrite($fh, $string);
    @fclose($fh);

} 

/**
 * Finally, send a JSON formatted response to SMSSync to 
 * acknowledge that the web service received the message.
 */
echo json_encode(array("payload"=>array(
    "success"=>$success)));

/**
 * Uncomment the code below to send an instant 
 * reply as SMS to the user.
 *
 * This feature requires the "Get reply from server" checked on SMSSync.
 */

/*
$msg = "Your message has been received";

$reply[0] = array("to" => $from, "message" => $msg);

echo json_encode(array("payload"=>array(
    "success"=>$success,
    "task"=>"send",
    "messages"=>array_values($reply))));
*/
</pre>
<p>
For a complete web service application, look at Ushahidi's <a href="https://github.com/ushahidi/Ushahidi_Web/tree/master/plugins/smssync">SMSSync plugin</a> which utilizes most of SMSSync's features.
</p>
<p>
There is also a SMSSync webservice for Django that implements most features. You can download it from <a href="https://github.com/cwanjau/SMSsync-Python-Django-webservice">GitHub.com</a>. Thanks to <a href="https://github.com/cwanjau">Caine Wanjau</a>
</p>
</div>

<p></div></div>
<footer class="footer">
    <div class="container">
        <div class="row-fluid">
            <div class="span4">
                Generated: 27-06-2013
            </div>
            <div class="span4">
                Copyright &copy; 2010 - 2013 <a href="http://www.ushahidi.com">Ushahidi.com</a>
            </div>
            <div class="span4">
                <p class="pull-right">
                    <span class="smssync">SMSSync</span><span>&nbsp;</span>  powered by <a href="http://www.ushahidi.com"><img src="images/ushahidi-logo.png"><span>Ushahidi</span</a>
                </p>
            </div>
        </div><br />
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
</body>
</html></p>
