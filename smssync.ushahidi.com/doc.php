<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SMSSync by Ushahidi</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link href="css/bootstrap.css" rel="stylesheet" type="text/css"/>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
<link href="libs/js/google-code-prettify/prettify.css" rel="stylesheet">

<script src="libs/js/google-code-prettify/prettify.js"></script>
</head>
<body>
<div class="topbar" data-scrollspy="scrollspy" >
    <div class="fill">
        <div class="container">
            <div id="logo">
                <h3>
                    <a href="#">SMSSync</a>
                </h3>
            </div>
            <ul class="nav">
                <li class="active"><a href="index.php">Home</a></li>
                <li><a href="#">News</a></li>
                <li><a href="#">Download</a></li>
                <li><a href="doc.php">Documentation</a></li>
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
    <h2>Instructions</h2>
    <ul>
        <li>To start the SMSSync Gateway, you'll need to specify a callback URL.
        This URL is where all incoming text messages will be transmitted to. 
        Remember to enter the full URL including the filename. A 
        typical example will be http://somedomain.com/index.php</li>
        <li>For  security you can specify a secret key at the callback URL. If 
        the secret key doesn't match, the callback URL will ignore 
        the transmission.</li>
        <li>Additionally, you can specify keywords with which to filter 
        incoming messages. Only matching text messages will be sent to 
        the SMSSync Gateway URL.</li>
		<li>SMSSync sends the following variables via the POST method:
		<ul>
		    <li><strong>from</strong></li>
			<li><strong>message</strong></li>
			<li><strong>secret</strong></li>
			<li><strong>sent_timestamp</strong></li>
	    </ul>
		</li>
	</ul>
    <p>
    In order for SMSSync to account for perfect transmission, the callback URL 
    needs to give back a formatted JSON string such as its shown below to 
    indicate if it received the message or not.<br /><br />
   <strong>Succeeded</strong> 
<pre class="prettyprint linenums">{
      payload: {
          success: "true"
      }
}</pre>
  
<br /><br />
   <strong>Failed</strong>
<pre class="prettyprint linenums">{
      payload: {
          success: "false"
      }
}</pre>
    </p>
	<p>&nbsp;</p>
	<strong>Response from server</strong>
<p>
SMSSync allows auto response to be configured on the app itself or to be 
retrieved from the server. When the app makes an HTTP Post request to sync the 
incoming messages to the configured URL,  it can send a JSON string that has 
messages in it as opposed to sending a success or failed JSON string as 
stated above. The app then sends the messages as SMS to users.

This makes it possible to have an instant response via SMS when an HTTP Post 
request is made. To leverage this feature, a JSON formatted string like the one 
below needs to be returned by the configured URL after the app makes the 
HTTP Post request. 

* *Response JSON data from the callback URL* 
<pre class="prettyprint linenums">{
    "payload": {
        "success": "true",
        "task": "send",
        "messages": [
            {
                "to": "000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "000-000-0000",
                "message": "the message goes here" 
            },
            {
                "to": "000-000-0000",
                "message": "the message goes here" 
            }
        ]
    }
}</pre>
	</p>
	<p>&nbsp;</p>
<strong>Task</strong>
<p>
SMSSync supports execution of task sent from the configured callback URL. At 
the moment, it supports sending of messages sent from the callback URL as SMS. 
This feature is targeted towards developers. If you are a developer and you want 
SMSSync to send an SMS, send a JSON formatted string as shown below to 
SMSSync with the variable task=send. When SMSSync does an HTTP GET request with 
a sample URL like this one, http://callback_url/smssync?task=send, it 
should bring back a JSON string below.</p>
<br /><br />		
<strong>Note:</strong> The secret key has to match the secret key 
configured with SMSSync, otherwise, SMSSync will not execute the task.
<br /><br />
	<strong>Response JSON data from the callback URL</strong>
<pre class="prettyprint linenums">{
	"payload": {
		"task": "send",
	   	"secret": "secret_key",
	   	"messages": [
	   		{
	   			"to": "000-000-0000",
	      		"message": "the message goes here" 
	   		},
	  		{
	    		"to": "000-000-0000",
	    		"message": "the message goes here" 
	   		},
	   		{
	        	"to": "000-000-0000",
	        	"message": "the message goes here" 
	   		}
	   	]
	}
}</pre>
<div class="container">
	<p>&nbsp;</p>
    <p><strong>Scan the QR below to install SMSSync on your Android powered phone</strong></p>
    <p><img src="http://qrcode.kaywa.com/img.php?s=6&#038;d=http%3A%2F%2Fmarket.android.com%2Fdetails%3Fid%3Dorg.addhen.smssync" alt="qrcode" /> 
    <p><strong>** We appreciate your feedback! **</strong></p>
</div>
<div class="container">
    <footer class="footer">
        <div class="container">

	        <p>Copyright © <?php echo date("Y"); ?> <a href="http://www.ushahidi.com">Ushahidi.com</a></p>
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
</html>
