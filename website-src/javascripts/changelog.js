var url = "http://query.yahooapis.com/v1/public/yql?q=select * from json where url='https://raw.github.com/ushahidi/SMSSync/master/changelog.json'&format=json";
$.ajax({
    url: url,
    cache : false,
    dataType : 'jsonp',
    success : fetchChangelog
});

// Get history details
function fetchChangelog(data) {
    var changelogs = data.query.results.json.json;
    $('#latest-header').html(Mustache.render($('#latest-header').html(), {"latest" : changelogs[0] }));
    $('#versions').html(Mustache.render($('#versions').html(), {"versions" : changelogs}));
}