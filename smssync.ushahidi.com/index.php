<!DOCTYPE html> 
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; char.uset=UTF-8" />
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
<script type="text/javascript">

    $(function() {
        $('#myCarousel').carousel();
    });
</script>
</head>
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
                    <li class="active"><a href="index">Home</a></li>
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
    <div class="row-fluid">
        <div class="span6">
            <br/><br />
            <h4>The free and open source SMS gateway for Android</h4>
            <p>It's one of the <strong><em>best</em></strong> SMS gateways for Android
            <p>The app is available today on the Android market. You can download it on your phone by scanning the QR code below.</p>
            <div class="thumbnails">
                <img class="thumbnail" width="150" src="http://qr.kaywa.com/?s=8&d=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3Dorg.addhen.smssync" alt="Scan to download SMSSync" title="Scan to download SMSSync"/>
            </div>
        </div>
        <div class="span6">
            <div id="myCarousel" class="carousel slide slid">
                <div class="carousel-inner">
                    <div class="item active">
                        <img src="images/promo/tablet_phone.png" alt="manage Sync URL" title="manage Sync URL">
                    </div>
                
                    <div class="item">
                        <img src="images/promo/pending.png" alt="pending" title="Pending Messages">
                    </div>
                
                    <div class="item">
                        <img src="images/promo/import.png" alt="import sms" title="Import SMS">
                    </div>
                
                    <div class="item">
                        <img src="images/promo/settings.png" alt="settings" title="Settings">
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
<!-- header ends -->
<!-- body starts -->

<div class="container">
<div class="container">
<!-- About SMSSync -->
<section id="about">
    <div class="page-header">
        <h1>About</h1>
    </div>
    <div class="row"><!--start row-->
        <div class="span4">
                <h3>What is it</h3>
                <p>
                    SMSSync is a simple, yet powerful SMS to HTTP sync utility,that turns any Android phone into a local SMS gateway by sending incoming messages(SMS) to a configured URL(web service).
                </p>
            </div>
            <div class="span4"> 
                <h3>License</h3>
                <p>
                SMSSync is licensed under <a href="http://www.gnu.org/licenses/lgpl-3.0.txt">LGPL</a> 
                (GNU Lesser General Public License, v.3). To read details about the 
                LGPL <a href="https://github.com/ushahidi/SMSSync/blob/master/LICENSE">click here.</a> 
                <!--TODO:// let this link to the license doc -->
                </p>
            </div> 
            <div class="span4"> 
                <h3>Participation</h3> 
                <p>There are a variety of ways to actively participate in the project. These range from writing documentation for the project to  translating the app into various other languages. If you are a developer and want to help write code for the project, feel free to browse through our <a href="https://github.com/ushahidi/SMSSync/issues/">issue tracker</a> on github.
                </p>
            </div>
        </div><!--end row-->
    </section>
    <section id="project-status">
        <div class="row"><!--start row-->
            <div class="span4">
                <h3>Release status</h3>
                <p>
                    <strong>Public release:</strong>
                        <ul>
                            <li>Released October 22<sup>nd</sup>, 2012</li>
                            <li>Version <a href="https://github.com/ushahidi/SMSSync/zipball/v2.0.1">v2.0.1</a></li>
                        </ul>
                    <strong>Stable branch:</strong>
                        <ul>
                            <li>
                                <a href="https://github.com/ushahidi/SMSSync/tree/master">master branch</a>
                            </li>
                        </ul>
                    <strong>Development branch:</strong>
                        <ul>
                        <li>
                            <a href="https://github.com/ushahidi/SMSSync">develop branch</a> open for commit/contributions for SMSSync 2.0.2</li>
                        </ul>
                        <a href="releases">More</a>
                </p>
            </div>
            <div class="span4"> 
                <h3>Tools</h3>
                <p>
                    SMSSync uses <a href="http://git-scm.com/">GIT</a> for source control management and the code 
                    is hosted on <a href="https://github.com/ushahidi/SMSSync/">github.com.</a> 
                </p>
                <p>
                    It uses <a href="http://github.com">Github</a> 
                    for project management and for tracking 
                    <a href="https://github.com/ushahidi/SMSSync/issues/">bugs and issues</a>. Transifex, the free and open source localization tool, is used for translations.
                </p>
            </div>
            <div class="span4"> 
                <h3>Translations</h3>
                <p>
                    SMSSync, as of now, has been completely translated into 5 languages. We use transifex for hosting our strings and to make translation much easier. 
                    <p>To help translate SMSSync into other languages, simply headover to 
                    <a href="https://www.transifex.net/projects/p/smssync/resource/stringsxml/">transifex.net</a>, 
                    add a language if it's not there or contribute to the uncompleted
                    translations.
                </p>            
            </div> 
        </div><!--end row-->
    </section>
</div></div>

<footer class="footer">
    <div class="container">
        <div class="row-fluid">
            <div class="span4">
                Generated: 22-10-2012
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
