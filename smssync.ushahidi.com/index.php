<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SMSSync by Ushahidi</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link href="css/styles.css" rel="stylesheet" type="text/css" />
</head>
<body>
<div id="header"><img src="images/smssync_logo.png" width="251" height="88" /></div>
<div id="content">
	<h2>Instructions</h2>
	<ul>
		<li>To start the SMSSync Gateway, you'll need to specify a callback URL. This URL is where all incoming text messages will be transmitted to.</li>
		<li>For  security you can specify at secret at the callback URL. If the secret doesn't match, the callback URL will ignore the transmission.</li>
		<li>Additionally, you can specify keywords with which to filter incoming messages. Only matching text messages will be sent to the SMSSync Gateway URL.</li>
		<li>The SMSSync sends the following variables via the POST method:
			<ul>
				<li><strong>from</strong></li>
				<li><strong>message</strong></li>
				<li><strong>secret</strong></li>
				<li><strong>sent_timestamp</strong></li>
			</ul>
		</li>
	</ul>
    <p>
    In order for SMSSync to account for perfect transmission, the callback URL needs to give back a formatted JSON string such as its shown below to indicate if it received the message or not.<br /><br />
   <strong>Succeeded</strong> 
<pre><code>{
      payload: {
          success: "true"
      }
}</code></pre>
  
<br /><br />
   <strong>Failed</strong>
<pre><code>{
      payload: {
          success: "false"
      }
}</code></pre>
    </p>
	<p>&nbsp;</p>
	<strong>Response from server</strong>
<p>
SMSSync allows auto response to be configured on the app itself or to be retrieved from the server. When the app makes an HTTP Post request to sync the incoming messages to the configured URL,  it can send a JSON string that has messages in it as opposed to sending a success or failed JSON string as stated above. The app then sends the messages as SMS to users.

This makes it possible to have an instant response via SMS when an HTTP Post request is made. To leverage this feature, a JSON formatted string like the one below needs to be returned by the configured URL after the app makes the HTTP Post request. 

* *Response JSON data from the callback URL* 
<pre><code>{
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
}</code></pre>
	</p>
	<p>&nbsp;</p>
<strong>Task</strong>
<p>
SMSSync supports execution of task sent from the configured callback URL. At the moment, it supports sending of messages sent from the callback URL as SMS. This feature is targeted towards developers. If you are a developer and you want SMSSync to send an SMS, send a JSON formatted string as shown below to SMSSync with the variable task=send. When SMSSync does an HTTP GET request with a sample URL like this one, http://callback_url/smssync?task=send, it should bring back a JSON string below.</p>
<br /><br />		
<strong>Note:</strong> The secret key has to match the secret key configured with SMSSync, otherwise, SMSSync will not execute the task.
<br /><br />
	<strong>Response JSON data from the callback URL</strong>
<pre><code>{
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
}</code></pre>
	<p>&nbsp;</p>
    <p><strong>Scan the QR below to install SMSSync on your Android powered phone</strong></p>
    <p><img src="http://qrcode.kaywa.com/img.php?s=6&#038;d=http%3A%2F%2Fmarket.android.com%2Fdetails%3Fid%3Dorg.addhen.smssync" alt="qrcode" /> 
<ul> 
	<p><strong>** We appreciate your feedback! **</strong></p>
<p>&nbsp;</p>
	<p>Copyright © <?php echo date("Y"); ?> <a href="http://www.ushahidi.com">Ushahidi.com</a></p>
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
