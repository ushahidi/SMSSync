<!DOCTYPE html> 
<html lang="en"><head>
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
                <li class="active"><a href="http://smssync.ushahidi.com/">Home</a></li>
                <li><a href="http://dev.ushahidi.com/projects/SMSSync/news">News</a></li>
                <li><a href="http://smssync.ushahidi.com/download/">Download</a></li>
                <li><a href="http://smssync.ushahidi.com/doc/">Documentation</a></li>
                <li><a href="http://forums.ushahidi.com/forum/ushahidi-apps">Support</a></li>
                <li><a href="http://smssync.ushahidi.com/contact/">Contact</a></li>
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
    <!-- About SMSSync -->
    <section id="about">
        <div class="page-header">
            <h1>About</h1>
        </div>
        <div class="row"><!--start row-->
            <div class="span-one-third">
                <h3>What is it</h3>
                <p>
                    SMSSync is a simple, yet powerful SMS to HTTP sync utility,
that turns any Android phone into a local SMS
gateway by sending incoming messages(SMS) to a configured URL(web service).

                </p>
            </div>
            <div class="span-one-third"> 
                <h3>License</h3>
                <p>
                SMSSync is licensed under <a href="http://www.gnu.org/licenses/lgpl-3.0.txt">LGPL</a> 
                (GNU Lesser General Public License, v.3). To read details about the 
                LGPL <a href="https://github.com/ushahidi/SMSSync/blob/master/LICENSE">click here.</a> 
                <!--TODO:// let this link to the license doc -->
                </p>
            </div> 
            <div class="span-one-third"> 
                <h3>Participation</h3> 
                <p>
                    There are a variety of ways to actively participate in the project. 
                    These range from writing documentation for the project to 
                    translating the app into various 
                    other languages. If you are a developer and want to help write code 
                    for the project, feel free to browse through our <a href="http://dev.ushahidi.com/projects/SMSSync/issues">issue 
                    tracker</a> on our redmine install.
                </p>            
            </div> 
        </div><!--end row-->
    </section>
    <section id="project-status">
        <div class="row"><!--start row-->
            <div class="span-one-third">
                <h3>Release status</h3>
                <p>
                    <strong>Public release:</strong>
                        <ul>
                            <li>Released October 27, 2011</li>
                            <li>Revision r9</li>
                            <li>Version <a href="http://dev.ushahidi.com/versions/show/42">1.0.8</a></li>
                        </ul>
                    <strong>Stable branch:</strong>
                        <ul>
                            <li>None</li>
                        </ul>
                    <strong>Development branch:</strong>
                        <ul>
                        <li><a href="https://github.com/ushahidi/SMSsync/">master branch</a> open for commit/contributions for SMSSync 1.0.9</li>
                        </ul>
                        <a href="http://dev.ushahidi.com/projects/SMSSync/news">More</a>
                </p>
            </div>
            <div class="span-one-third"> 
                <h3>Tools</h3>
                <p>
                    SMSSync uses <a href="http://git-scm.com/">GIT</a> for source control management and the code 
                    is hosted on <a href="https://github.com/ushahidi/SMSSync/">github.com.</a> 
                </p>
                <p>
                    It uses <a href="http://redmine.org">Redmine</a> 
                    for project management and for tracking 
                    <a href="http://dev.ushahidi.com/projects/SMSSync/issues">bugs and issues</a>. 
                    Transifex, the free and open source localization tool, is used for translations.
                </p>
            </div> 
            <div class="span-one-third"> 
                <h3>Translations</h3> 
                <p>
                    SMSSync, as of now, has been completely translated into 5 languages. We use 
                    transifex for hosting our strings and to make translation much easier. 

                    <p>To help translate SMSSync into other languages, simply headover to 
                    <a href="https://www.transifex.net/projects/p/smssync/resource/smssyncpo/">transifex.net</a>, 
                    add a language if it's not there or contribute to the uncompleted
                    translations.
                </p>            
            </div> 
        </div><!--end row-->

    </section>
</div>
<div class="container">
<footer class="footer">
    <div class="container">
        <div class="pull-right">
            powered by <a href="http://www.ushahidi.com">Ushahidi</a>
        </div> 
	    <p>Copyright &copy; 2010 - <?php echo date("Y"); ?> <a href="http://www.ushahidi.com">Ushahidi.com</a></p>
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
