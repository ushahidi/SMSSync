+++
date = "2015-05-01T16:26:41+09:00"
title = "Developers"
header = "<h1>Developers Documentation <small>This shows how to integrate SMSsync into your project.</small> </h1>"
+++
<div class="row">
    <div class="col-lg-6">
        <h2>Instructions</h2>
        <ul>
            <li>
                SMSsync uses the <a href="http://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol">HTTP</a>
                and <a href="http://en.wikipedia.org/wiki/HTTPS">HTTPS</a> protocols for
                communication.<br/>
                To start the SMSsync Gateway, you'll need to specify a Sync URL. This URL is where
                all incoming SMS will be transmitted to. Remember to enter the full URL including
                the filename. A typical example will be <code>http://somedomain.com/index.php</code>
            </li>
            <li>
                For security you can specify a secret key to be sent to the Sync URL. If the secret
                key doesn't match on the server, the Sync URL can ignore the transmission.
            </li>
            <li>
                Additionally, you can specify keywords with which to filter incoming SMS. Only
                matching messages will be forwarded to the SMSsync Gateway URL.
            </li>
            <li>
                SMSsync uses the following variables to transmit the incoming SMS via the POST
                method:
                <ul>
                    <li><strong>from</strong> -- the number that sent the SMS</li>
                    <li><strong>message</strong> -- the SMS sent</strong></li>
                    <li><strong>message_id</strong> -- the unique ID of the SMS</li>
                    <li><strong>sent_to</strong> -- the phone number registered on the SIM card
                        otherwise it's the value set on the app as device ID
                    </li>
                    <li><strong>secret</strong> -- the secret key set on the app</li>
                    <li><strong>device_id</strong> -- the unique id set on the device to be used by
                        the server to identify which device is communicating with it. Note:
                        supported from v2.6.1 and above
                    </li>
                    <li><strong>sent_timestamp</strong> -- the timestamp the SMS was sent. In the
                        UNIX timestamp format</strong></li>
                </ul>
            </li>
        </ul>
        <p>
            In order for SMSsync to ensure perfect transmission, the Sync URL must return a
            JSON-formatted status message, as shown below.
            <br/><br/>
            <strong>Succeeded</strong>
<pre class="prettyprint linenums">
{
    "payload":
    {
        "success": true,
        "error": null
    }
}</pre>
        <br/><br/>
        <strong>Failed</strong>
        <br/>
<pre class="prettyprint linenums">
{
    "payload":
    {
        "success": false,
        "error": "error message from the server"
    }
}</pre>
        </p>
        <p>&nbsp;</p>
        <strong>Response from server</strong>
        <p>
            SMSsync allows either an auto-response message to be configured on the app itself, or to
            be retrieved from the server. When the app makes an HTTP Post request to sync the
            incoming SMS to the configured URL, the server can respond with JSON-encoded messages
            alongside the success message. The app then sends these messages by SMS to the specified
            users phone.
        </p>
        <p>
            This makes it possible to have an instant response via SMS when an HTTP Post request is
            made. To leverage this feature, a JSON formatted string like the one below needs to be
            returned by the configured URL in response to the app's HTTP Post request.
        </p>
        <p>
            In the app itself, ensure <strong>*Get Reply from Server*</strong> is checked to enable
            this feature.
        <p>
            <strong>Response JSON data from the Sync URL</strong>
<pre id="code" class="prettyprint linenums">{
    "payload": {
        "success": "true",
        "task": "send",
        "messages": [
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "042b3515-ef6b-f424-c4qd"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "026b3515-ef6b-f424-c4qd"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "096b3515-ef6b-f424-c4qd"
            }
        ]
    }
}</pre>
        </p>
        <p>&nbsp;</p>
        <strong>Task</strong>
        <p>
            SMSsync supports execution of tasks defined on the server. Currently it supports sending
            of messages sent from the Sync URL as SMS. This feature is targeted towards developers.
            The app can be configured to poll the server for new tasks at a given frequency. The
            server then needs to respond to HTTP GET requests with <code>?task=send</code> (for
            example <code>http://callback_url/smssync?task=send</code>). The format of this response
            is shown below.
        </p>
        <br/><br/>
        <p><strong>Response JSON data from the Sync URL</strong></p>
<pre class="prettyprint linenums">{
    "payload": {
        "task": "send",
        "secret": "secret_key",
        "messages": [
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "042bf515-eq6b-f424-c4pz"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "022b3515-ef6b-f424-c4ws"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "042b3515-ef6b-f424-c4qd"
            }
        ]
    }
}</pre>
        <br/><br/>
        <strong>Notes:</strong>
        <ul>
            <li>
                The secret key provided by the server must match the secret key configured within
                SMSsync, otherwise SMSsync will not execute the task.
            </li>
            <li>
                To ensure the message is sent to the correct recipient, add the country code to the
                phone number. Eg. <strong>+254</strong>700709142. Without this, the message is sent
                to the number in the country where the phone is.
            </li>
            <li>
                The web service should check the value of the secret key passed with each task
                request that SMSsync makes to it for messages to send and respond appropriately to
                ensure that not any instance of SMSsync can communicate with it.
            </li>
        </ul>
        <a name="message-results"><strong>Message Results API</strong></a>
        <p>Message Results API is a way to get SMS status delivery report back to the server so the
            server knows that messages have been
            successfully sent to their respective recipients or not.</p>
        <p>This feature is supported on v2.7 and above. To make use of this feature, you have to
            enable the <code>Message Results API</code> and <code>SMS Delivery Report</code> from
            the Settings screen.</p>
        <p>SMSsync will periodically send a Task request to the server for messages to send as SMS.
            <strong>Note:</strong> The server needs to include a unique ID for the messages in the
            JSON response as <code>"uuid":"unique_id"</code> key. See below for a sample JSON
            response from the server</p>
<pre class="prettyprint linenums">
GET /smssync?task=send HTTP/1.1
Host: testserver.local

HTTP/1.1 200 OK
Server: nginx/1.5.2
Content-Type: application/json; charset=utf-8
{
    "payload": {
        "task": "send",
        "secret": "secret_key",
        "messages": [
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "aada21b0-0615-4957-bcb3"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "1ba368bd-c467-4374-bf28"
            },
            {
                "to": "+000-000-0000",
                "message": "the message goes here",
                "uuid": "95df126b-ee80-4175-a6fb"
            }
        ]
    }
}</pre>
        Once SMSsync receives messages to be sent as SMS it will make a <code>POST ?task=sent</code>
        request with the message UUIDs as below to the server to acknowledge that it has received
        the messages
        and has queued them up for processing.
<pre class="prettyprint linenums">
POST /smssync?task=sent HTTP/1.1
Host: testserver.local

HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
{
    "queued_messages": [
        "aada21b0-0615-4957-bcb3",
        "1ba368bd-c467-4374-bf28",
        "95df126b-ee80-4175-a6fb"
    ]
}</pre>
        <p>Next, the server needs to process the JSON response sent by SMSsync. It needs to remove
            all the message UUIDs sent by SMSsync from
            the subsequent outgoing messages list so SMSsync avoids processing those again. Once the
            server receives the UUIDs of the queued up messages, it needs to send a response
            back to SMSsync as JSON response with the received messages UUIDs as JSON array. If
            there are no received messages UUIDs, it needs to send back an empty message_uuids JSON
            array to SMSsync</p>
<pre class="prettyprint linenums">
POST /smssync?task=sent HTTP/1.1
Host: testserver.local

HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
{
    "message_uuids": [
        "aada21b0-0615-4957-bcb3",
        "1ba368bd-c467-4374-bf28",
        "95df126b-ee80-4175-a6fb"
    ]
}</pre>
        <p>Sample empty message uuids JSON response.</p>
<pre class="prettyprint linenums">
POST /smssync?task=sent HTTP/1.1
Host: testserver.local

HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
{
    "message_uuids": []
}</pre>
        <p>To send SMS status delivery report back to the server, SMSsync will make a <code>GET
            ?task=result</code> to the server and should receive a list
            of message UUIDs that are waiting to receive delivery reports. The server should send
            the JSON response below</p>
<pre class="prettyprint linenums">
GET /smssync?task=result HTTP/1.1
Host: testserver.local

HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8
{
    "message_uuids": [
        "aada21b0-0615-4957-bcb3",
        "1ba368bd-c467-4374-bf28",
        "95df126b-ee80-4175-a6fb"
    ]
}</pre>
        <p>Then SMSsync will send delivery reports for the message uuids above as a POST
            request<code>POST ?task=result</code> with the JSON response below</p>
<pre class="prettyprint linenums">
POST /smssync?task=result HTTP/1.1
Host: testserver.local
Content-Type: application/json; charset=utf-8

{
    "message_result": [
        {
            "uuid": "052bf515-ef6b-f424-c4ee",
            "sent_result_code": 0,
            "sent_result_message": "SMSSync Message Sent"
            "delivered_result_code": -1,
            "delivered_result_message": ""
        },
        {
            "uuid": "aada21b0-0615-4957-bcb3",
            "sent_result_code": 0,
            "sent_result_message": "SMSSync Message Sent",
            "delivered_result_code": 0,
            "delivered_result_message": "SMS Delivered"
        },
        {
            "uuid": "1ba368bd-c467-4374-bf28",
            "sent_result_code": 1,
            "sent_result_message": "Failed to send SMS - Maybe insufficient air time on the phone.",
            "delivered_result_code": -1,
            "delivered_result_message": ""
        },
        {
            "uuid": "95df126b-ee80-4175-a6fb",
            "sent_result_code": 4,
            "sent_result_message": "No service",
            "delivered_result_code": -1,
            "delivered_result_message": ""
        }
    ]
}</pre>
    </div>
    <div class="col-lg-6">
        <h2>A sample web service</h2>
        <p>This is a sample PHP script to demonstrate how to write a webservice to successfully
            communicate with SMSsync.</p>
<pre class="prettyprint linenums">
/**
 * Gets the messages(SMSs) sent by SMSsync as a POST request.
 *
 */
function get_message()
{
    $error = NULL;
    // Set success to false as the default success status
    $success = false;
    /**
     *  Get the phone number that sent the SMS.
     */
    if (isset($_POST['from']))
    {
        $from = $_POST['from'];
    }
    else
    {
        $error = 'The from variable was not set';
    }
    /**
     * Get the SMS aka the message sent.
     */
    if (isset($_POST['message']))
    {
        $message = $_POST['message'];
    }
    else
    {
        $error = 'The message variable was not set';
    }
    /**
     * Get the secret key set on SMSsync side
     * for matching on the server side.
     */
    if (isset($_POST['secret']))
    {
        $secret = $_POST['secret'];
    }
    /**
     * Get the timestamp of the SMS
     */
    if(isset($_POST['sent_timestamp']))
    {
        $sent_timestamp = $_POST['sent_timestamp'];
    }
    /**
     * Get the phone number of the device SMSsync is
     * installed on.
     */
    if (isset($_POST['sent_to']))
    {
        $sent_to = $_POST['sent_to'];
    }
    /**
     * Get the unique message id
     */
    if (isset($_POST['message_id']))
    {
        $message_id = $_POST['message_id'];
    }
    /**
     * Get device ID
     */
    if (isset($_POST['device_id']))
    {
        $device_id = $_POST['device_id'];
    }
    /**
     * Now we have retrieved the data sent over by SMSsync
     * via HTTP. Next thing to do is to do something with
     * the data. Either echo it or write it to a file or even
     * store it in a database. This is entirely up to you.
     * After, return a JSON string back to SMSsync to know
     * if the web service received the message successfully or not.
     *
     * In this demo, we are just going to save the data
     * received into a text file.
     *
     */
    if ((strlen($from) > 0) AND (strlen($message) > 0) AND
        (strlen($sent_timestamp) > 0 )
        AND (strlen($message_id) > 0))
    {
        /* The screte key set here is 123456. Make sure you enter
         * that on SMSsync.
         */
        if ( ( $secret == '123456'))
        {
            $success = true;
        } else
        {
            $error = "The secret value sent from the device does not match the one on the server";
        }
        // now let's write the info sent by SMSsync
        //to a file called test.txt
        $string = "From: ".$from."\n";
        $string .= "Message: ".$message."\n";
        $string .= "Timestamp: ".$sent_timestamp."\n";
        $string .= "Messages Id:" .$message_id."\n";
        $string .= "Sent to: ".$sent_to."\n";
        $string .= "Device ID: ".$device_id."\n\n\n";
        write_message_to_file($string);
    }
    /**
     * Comment the code below out if you want to send an instant
     * reply as SMS to the user.
     *
     * This feature requires the "Get reply from server" checked on SMSsync.
     */
     send_instant_message($from);
    /**
      * Now send a JSON formatted string to SMSsync to
      * acknowledge that the web service received the message
      */
     $response = json_encode([
        "payload"=> [
            "success"=>$success,
                "error" => $error
            ]
        ]);
     //send_response($response);
}

/**
 * Writes the received responses to a file. This acts as a database.
 */
function write_message_to_file($message)
{
    $myFile = "test.txt";
    $fh = fopen($myFile, 'a') or die("can't open file");
    @fwrite($fh, $message);
    @fclose($fh);
}

/**
 * Implements the task feature. Sends messages to SMSsync to be sent as
 * SMS to users.
 */
function send_task()
{
    /**
     * Comment the code below out if you want to send an instant
     * reply as SMS to the user.
     *
     * This feature requires the "Get reply from server" checked on SMSsync.
     */
    if (isset($_GET['task']) AND $_GET['task'] === 'send')
    {
        $m = "Sample Task Message";
        $f = "+000-000-0000";
        $s = "true";
        $reply[0] = [
            "to" => $f,
            "message" => $m,
            "uuid" => "1ba368bd-c467-4374-bf28"
        ];
        // Send JSON response back to SMSsync
        $response = json_encode(
            ["payload"=>[
                "success"=>$s,
                "task"=>"send",
                "secret" => "123456",
                "messages"=>array_values($reply)]
            ]);
        send_response($response);
    }
}

/**
 * This sends an instant response when the server receive messages(SMSs) from
 * SMSsync. This requires the settings "Get Reply from Server" enabled on
 * SMSsync.
 */
function send_instant_message($to)
{
    $m = "Your message has been received";
    $f = "+000-000-0000";
    $s = true;
    $reply[0] = [
        "to" => $to,
        "message" => $m,
        "uuid" => "1ba368bd-c467-4374-bf28"
    ];
    // Send JSON response back to SMSsync
    $response = json_encode(
        ["payload"=>[
            "success"=>$s,
            "task"=>"send",
            "secret" => "123456",
            "messages"=>array_values($reply)]
        ]);
    send_response($response);
}

function send_response($response)
{
    // Avoid caching
    header("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
    header("Expires: Sat, 26 Jul 1997 05:00:00 GMT"); // Date in the past
    header("Content-type: application/json; charset=utf-8");
    echo $response;
}

function get_sent_message_uuids()
{
    $data = file_get_contents('php://input');
    $queued_messages = file_get_contents('php://input');
    // Writing this to a file for demo purposes.
    // In production, you will have to process the JSON string
    // and remove the messages from the database or where ever the
    // messages are stored so the next Task run, the server won't add
    // these messages.
    write_message_to_file($queued_messages."\n\n");
    send_message_uuids_waiting_for_a_delivery_report($queued_messages);

}

/**
 * Sends message UUIDS to SMSsync for their sms delivery status report.
 * When SMSsync send messages from the server as SMS to phone numbers, SMSsync
 * can send back status delivery report for these messages.
 */
function send_message_uuids_waiting_for_a_delivery_report($queued_messages)
{
    // Send back the received messages UUIDs back to SMSsync
    $json_obj = json_decode($queued_messages);
    $response = json_encode(
    [
        "message_uuids"=>$json_obj->queued_messages
    ]);
    send_response($response);
}

function send_messages_uuids_for_sms_delivery_report()
{
    if(isset($_GET['task']) AND $_GET['task'] == 'result'){
        $response = json_encode(
        [
            "message_uuids" => ['1ba368bd-c467-4374-bf28']
        ]);
        send_response($response);
    }

}

/**
 * Get status delivery report on sent messages
 *
 */
function get_sms_delivery_report()
{
    if($_GET['task'] === 'result' AND $_GET['secret']=== '123456')
    {
        $message_results = file_get_contents('php://input');
        write_message_to_file("message ".$message_results."\n\n");
    }
}

// Execute functions above
if($_SERVER['REQUEST_METHOD'] === 'POST')
{
    if(isset($_GET['task']) AND $_GET['task'] === 'result'){
        get_sms_delivery_report();
    }
    else if( isset($_GET['task']) && $_GET['task'] === 'sent')
    {
        get_sent_message_uuids();
    }
    else
    {
        get_message();
    }
}
else
{
    send_task();
    send_messages_uuids_for_sms_delivery_report();
}
</pre>
        <p>
            Assuming you've the above code saved in a file called demo.php and is located at your
            web server's document root, you can issue the command below to test.
        </p>
<pre class="prettyprint linenums">
    $ curl -D - -X POST http://localhost/demo.php \
        -F "from=+000-000-0000" \
        -F "message=sample text message" \
        -F "secret=123456" \
        -F "device_id=1" \
        -F "sent_timestamp=123456789" \
        -F "message_id=80" \
</pre>
        <p>
            The server should return a JSON response indicating a success:true or success:false
        </p>
        <p>
            For a complete web service application, look at Ushahidi's <a
                href="https://github.com/ushahidi/Ushahidi_Web/tree/master/plugins/smssync">SMSsync
            plugin</a> which utilizes most of SMSsync's features.
        </p>
        <p>
            There is also SMSsync webservice for Django that implements most of the features. You
            can download it from <a
                href="https://github.com/cwanjau/SMSsync-Python-Django-webservice">GitHub.com</a>.
            Thanks to <a href="https://github.com/cwanjau">Caine Wanjau</a>
        </p>
        <p>&nbsp;</p>
        <a name="alerts"><strong>Alerts</strong></a>
        <p>As of v2.7 and above, you should be able to query for the status of the device running
            SMSsync with query codes. For example, if you want to know if the device can still reach
            the web server, just text @20 to the device and you should receive a text message back
            with the status of the server.</p>
        Below are the query codes supported at the moment.
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <th>Query code @xx</th>
                <th>Query</th>
                <th>+ Query response</th>
                <th>- Query response</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td><code>@10</code></td>
                <td>Is cell reception ok</td>
                <td><span class="text-success">cell reception ok</span></td>
                <td><span class="text-warning">The phone sends no response.</span></td>
            </tr>
            <tr>
                <td><code>@20</code></td>
                <td>Is server ok</td>
                <td><span class="text-success">server responded with <HTTP Status Code> status
                    code.Eg. server responded with 200 status code</span></td>
                <td><span class="text-warning">Cannot reach server.</span></td>
            </tr>
            <tr>
                <td><code>@30</code></td>
                <td>Battery level</td>
                <td><span class="text-success">Battery level is <percentage of battery level> Eg.
                    battery level is 10%</span></td>
                <td><span class="text-warning">The phone sends no response.</span></td>
            </tr>
            <tr>
                <td><code>@40</code></td>
                <td>Get all statuses</td>
                <td><span class="text-success">All the query's positive responses</span></td>
                <td><span class="text-warning">All the query's negative responses</span></td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
