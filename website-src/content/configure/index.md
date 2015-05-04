+++
date = "2015-05-01T14:01:52+09:00"
title = "Configure"
header = "<h1>How To Configure SMSsync<small> This page documents how to configure SMSsync on your Android device.</small></h1>"
+++
<div class="container bs-docs-container">
    <div class="row">
        <div class="col-md-3">
            <div class="bs-sidebar hidden-print" role="complementary">
                <ul class="nav bs-sidenav">
                    <li>
                        <a href="#overview">Overview</a>
                    </li>
                    <li>
                        <a href="#manage-sync-urls">Manage Sync URLs</a>
                        <ul class="nav">
                            <li>
                                <a href="#add-new-sync-url">Add new Sync URL</a>
                            </li>
                            <li>
                                <a href="#edit-sync-url">Edit a Sync URL</a>
                            </li>
                            <li>
                                <a href="#change-sync-scheme">Change Sync Scheme</a>
                            </li>
                            <li>
                                <a href="#delete-all-existing-sync-url">Delete All Existing Sync URLs</a>
                            </li>
                            <li>
                                <a href="#delete-an-existing-sync-url">Delete An Existing Sync URL</a>
                            </li>
                            <li>
                                <a href="#start-smssync-service">Start SMSsync service</a>
                            </li>
                            <li>
                                <a href="#stop-smssync-service">Stop SMSsync service</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="#preferences">Preferences</a>
                        <ul class="nav">
                            <li>
                                <a href="#general-settings">General Settings</a>
                            </li>
                            <li>
                                <a href="#reply-message-settings">Reply Message Settings</a>
                            </li>
                            <li>
                                <a href="#auto-sync-settings">Auto Sync Settings</a>
                            </li>
                            <li>
                                <a href="#task-settings">Task Settings</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="#manage-pending-messages">Manage Pending Messages</a>
                        <ul class="nav">
                            <li>
                                <a href="#manually-sync-all-pending-messages">Manually sync all pending messages</a>
                            </li>
                            <li>
                                <a href="#manually-sync-a-single-pending-message">Manaully sync a single pending message</a>
                            </li>
                            <li>
                                <a href="#delete-pending-messages">Delete pending messages</a>
                            </li>
                            <li>
                                <a href="#import-sms">Import SMS</a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="#manage-sent-messages">Manage Sent Messages</a>
                        <ul class="nav">
                            <li>
                                <a href="#delete-an-individual-sent-message">Delete An Individual Sent Message</a>
                            </li>
                            <li>
                                <a href="#delete-all-sent-messages">Delete All Sent Messages</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
        <div class="col-md-9" role="main">
            <div class="bs-docs-section">
                <div class="page-header">
                    <h1 id="overview">Overview</h1>
                </div>
                This how to covers the essential configuration to get SMSsync up and running. The application has been designed to be intuitive enough to easily get it running.
                <strong>**Note:**</strong> SMSsync works on any <acronym title="Short Message Service">SMS</acronym>-enabled device running Android 2.1 and above.
            </div>
            <div class="bs-docs-section">
                <div class="page-header">
                    <h1 id="manage-sync-urls">Manage Sync URLs</h1>
                </div>
                Here you can manage multiple Sync URLs. Sync URLs are the URLs of your webservices. These are where the messages from SMSsync are actually pushed to.
                <h3 id="add-new-sync-url">Add new Sync URL</h3>
                <p>To add a new Sync URL:</p>
                <ol>
                    <li>Tap on the <strong><em>Sync URL</em></strong> from the navigation drawer.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/new.png" alt="Add icon"/> icon on the actionbar. An input dialog should open.</li>
                    <li>Enter a title for the Sync URL.</li>
                    <li>Enter a secret key if required by the webservices. Make sure you enter the exact key here. The secret key should be presented as string of any characters without spaces.</li>
                    <li>Enter a comma separated value for the keyword(s). These keywords will be used by SMSsync to filter incoming SMS and pending messages to the Sync URL you are adding. As of v2.0.2. You can now add Regular Expresssion code for filtering. This means, it can either be CSV or RegExp. It cannot be both.</li>
                    <li>Enter the URL for your webservice. Don&#39;t forget to start with the <strong><em>HTTP</em></strong> or <strong><em>HTTPS</em></strong> protocol. e.g. <code>https://example.com/api-v1/add-record/</code></li>
                    <li>Tap <strong><em>OK</em></strong> to save the new entry.</li>
                </ol>
                <p>Note: Version 2.5 or higher supports <a href="http://en.wikipedia.org/wiki/Basic_access_authentication">basic auth</a> credentials in the URL, e.g. <code>https://username:pass@example.com/api-v1/add-record/</code>.</p>
                <h3 id="edit-sync-url">Edit a Sync URL</h3>
                <p>To edit an existing Sync URL:</p>
                <ol>
                    <li>Long press on the Sync URL you want to edit by tapping and holding it until the actionbar menu changes.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/edit.png" alt="Edit icon"/> icon on the actionbar. An input dialog should open repopulated with the details of the Sync URL.</li>
                    <li>Edit the necessary field(s) accordinlgy.</li>
                    <li>Tap <strong><em>OK</em></strong> to save the edited entry.</li>
                </ol>
                <h3 id="change-sync-scheme">Change Sync Scheme</h3>
                <p>The default sync scheme uses URLEncoded. However, with <strong>version 2.5 and above</strong> you can now customize which sync scheme should be used for synchronization. The supported formats are URLEncoded, JSON, XML</p>
                <p>To change the sync scheme:</p>
                <ol>
                    <li>Long press on the Sync URL you want to edit by tapping and holding it until the actionbar menu changes.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/settings.png" alt="Change sync scheme" /> icon on the actionbar. A dialog will pop up.</li>
                    <li>Select the HTTP Method to use. POST and PUT are supported</li>
                    <li>Select the Data Format. URLEncoded, JSON and XML are supported for now.</li>
                    <li>Set the Payload Keys to use. You can only set 5 keys. Make sure the keys matches what is expected on the server. In most cases it is safe to stick to the default keys.</li>
                </ol>
                <h3 id="delete-all-existing-sync-url">Delete All Existing Sync URLs</h3>
                <p>Delete all Sync URL by:</p>
                <ol>
                    <li>If there are any Sync URLs enabled(checked), disable(uncheck) them.</li>
                    <li>Stop the SMSsync service if it&#39;s running(checked).</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/delete.png" alt="Delete icon"/> icon on the actionbar. A confirmation dialog should open.</li>
                    <li>Tap <strong><em>Yes</em></strong> to delete all Sync URL or <strong><em>No</em></strong> to cancel the deletion.</li>
                </ol>
                <h3 id="delete-an-existing-sync-url">Delete An Existing Sync URL</h3>
                <p>Delete an existing Sync URL by:</p>
                <ol>
                    <li>If the Sync URL to be deleted is enabled&nbsp;(checked), disable it. Otherwise, move to the next item.</li>
                    <li>Long press on the Sync URL you want to delete by tapping and holding it until the actionbar menu changes.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/delete.png" alt="Delete icon"/> icon on the actionbar. A confirmation dialog should open.</li>
                    <li>Tap <strong><em>Yes</em></strong> to delete it or <strong><em>No</em></strong> to cancel the deletion.</li>
                </ol>
                <h3 id="start-smssync-service">Start SMSsync service</h3>
                <p>To start the SMSsync service:</p>
                <ol>
                    <li>Make sure you have added and enabled&nbsp;(checked) at least one Sync URL.</li>
                    <li>On the <strong><em>SYNC URL</em></strong> screen, tap on the <strong><em>Start SMSsync service</em></strong> to start the service. You do this if the service is disabled.</li>
                </ol>
                <h3 id="stop-smssync-service">Stop SMSsync service</h3>
                <p>To stop the SMSsync service:</p>
                <ol>
                    <li>On the <strong><em>SYNC URL</em></strong> screen, uncheck the <strong><em>Start SMSsync service</em></strong> option to stop the service.</li>
                </ol>
            </div>
            <div class="bs-docs-section">
                <div class="page-header">
                    <h1 id="preferences">Preferences</h1>
                </div>
                <p>Using the options available in the app settings, you can configure the behaviour however you like.</p>
                <h3 id="general-settings">General Settings</h3>
                <ol>
                    <li>Go to Settings screen by tapping the <img src="{{urls.media}}/icons/settings.png" alt="Settings icon"/> icon on the actionbar or option menu.</li>
                    <li>Tap on <strong><em>Enter Unique ID</em></strong> to enter a Unique ID to identify the device SMSsync is installed on.</li>
                </ol>
                <h3 id="reply-message-settings">Reply Message Settings</h3>
                <p>This is where you set auto reply message to be sent to the user that sent SMS to SMSsync. You can specify a message on the phone or get the message from the server.
            To set a reply message:</p>
                <ol>
                    <li>Go to Settings screen by tapping the <img src="{{urls.media}}/icons/settings.png" alt="Settings icon"/> icon on the actionbar or option menu.</li>
                    <li>Tap on <strong><em>Get Reply From Server</em></strong> if you want to send an instant reply message from the Sync URL. Otherwise, tap on <strong><em>Reply Messages</em></strong> to set the instant reply message on the phone.</li>
                </ol>
                <h3 id="auto-sync-settings">Auto Sync Settings</h3>
                <p>This is where you enable SMSsync to periodically send pending messages to the enabled Sync URLs.
            To enable auto sync:</p>
                <ol>
                    <li>Go to Settings screen by tapping the <img src="{{urls.media}}/icons/settings.png" alt="Settings icon"/> icon on the actionbar or option menu.</li>
                    <li>Tap on <strong><em>Enable Auto Sync</em></strong> to enable the auto sync service.</li>
                    <li>Tap on <strong><em>Auto sync frequency</em></strong> to set the frequency at which the auto sync should run.</li>
                </ol>
                <h3 id="task-settings">Task Settings</h3>
                <p>The Task feature allows you to send SMS to users as defined by the server. It works by regularly checking the server for new messages to be sent.
            To enable Task Checking feature:</p>
                <ol>
                    <li>Go to Settings screen by tapping the <img src="{{urls.media}}/icons/settings.png" alt="Settings icon"/> icon on the actionbar or option menu.</li>
                    <li>Tap on <strong><em>Enable Task Checking</em></strong> to enable the task service.</li>
                    <li>Tap on <strong><em>Frequency</em></strong> to set the frequency at which the task checking service should run.</li>
                </ol>
            </div>
            <div class="bs-docs-section">
                <div class="page-header">
                    <h1 id="manage-pending-messages">Manage Pending Messages</h1>
                </div>
                <p>Pending messages are failed SMS that couldn&#39;t make it to any of the enabled Sync URLs.
            SMSsync allows you to manage these pending messages. You can either configure the app to automatically or manually sync these failed messages or delete them.</p>
                <h3 id="manually-sync-all-pending-messages">Manually sync all pending messages</h3>
                <ol>
                    <li>Tap on the <img src="{{urls.media}}/icons/refresh.png" alt="Sync icon"/> icon on the actionbar. The sync icon should spin for a while.</li>
                    <li>After the sync icon spins for a while, a Toast should show up with a status message.</li>
                </ol>
                <h3 id="manually-sync-a-single-pending-message">Manaully sync a single pending message</h3>
                <ol>
                    <li>Long press on the pending message you want to sync by tapping and holding it until the actionbar menu changes.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/refresh.png" alt="Sync icon"/> icon on the actionbar. The sync icon should spin for a while.</li>
                    <li>After the sync icon stops spinning, a popup should appear with a status message.</li>
                </ol>
                <h3 id="delete-pending-messages">Delete pending messages</h3>
                <p>Delete all pending messages saved on the device.</p>
                <ol>
                    <li>Tap on the <img src="{{urls.media}}/icons/delete.png" alt="Delete icon"/> icon on the actionbar. A confirmation dialog should open.</li>
                    <li>Tap <strong><em>Yes</em></strong> to delete all pending messages or <strong><em>No</em></strong> to cancel the deletion.</li>
                </ol>
                <h3 id="import-sms">Import SMS</h3>
                <p>Import SMS from the device&#39;s messages inbox.</p>
                <ol>
                    <li>Tap on the <img src="{{urls.media}}/icons/import.png" alt="Import icon"/> icon on the actionbar. A progress dialog should open. Leave it to run until it finishes. Depending on the number of messages in your inbox, this could take a while.</li>
                </ol>
            </div>
            <div class="bs-docs-section">
                <div class="page-header">
                    <h1 id="manage-sent-messages">Manage Sent Messages</h1>
                </div>
                <p>Sent messages are SMS or pending messages that have successfully been sync&#39;d to the enabled Sync URL. This is to allow you to see which messages were successfully sent. Here you can view and delete these sent messages. This section will show you how to delete sent messages individually or all at the same time.</p>
                <h3 id="delete-an-individual-sent-message">Delete An Individual Sent Message</h3>
                <ol>
                    <li>Long press on the sent message you want to delete by tapping and holding it until the actionbar menu changes.</li>
                    <li>Tap on the <img src="{{urls.media}}/icons/delete.png" alt="Delete icon"/> icon on the actionbar. A confirmation dialog should open.</li>
                    <li>Tap <strong><em>Yes</em></strong> to delete it or <strong><em>No</em></strong> to cancel the deletion.</li>
                </ol>
                <h3 id="delete-all-sent-messages">Delete All Sent Messages</h3>
                <ol>
                    <li>Tap on the <img src="{{urls.media}}/icons/delete.png" alt="Delete icon"/> icon on the actionbar. A confirmation dialog should open.</li>
                    <li>Tap <strong><em>Yes</em></strong> to delete all sent messages or <strong><em>No</em></strong> to cancel the deletion.</li>
                </ol>
            </div>
        </div>
    </div>
</div>
