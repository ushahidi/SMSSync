
var url = "http://query.yahooapis.com/v1/public/yql?q=select * from json where url='https://raw.github.com/ushahidi/SMSSync/develop/contributors.json'&format=json";
$.ajax({
    url: url,
    cache : false,
    dataType : 'jsonp',
    success : fetchContributors
});

$.ajax({
    url: url,
    cache : false,
    dataType : 'jsonp',
    success : displayContributors
});
// Get history details
function fetchContributors(data) {
    var map = L.mapbox.map('map', 'examples.map-9ijuk24y');
    var markers = new L.MarkerClusterGroup();
    var latLng  = new Array();
    var contributors = data.query.results.json.contributors;
    $.each(contributors, function(index,contributor){
        var title = normalizeNames(contributor);
        latLng[index] = new L.LatLng(contributor.country.latitude, contributor.country.longitude);
        var marker = L.marker(new L.LatLng(contributor.country.latitude, contributor.country.longitude), {
            icon: L.mapbox.marker.icon({'marker-symbol': 'marker','marker-color': '891f02'}),
            title: title
        });
        marker.bindPopup(title);
        markers.addLayer(marker);
    });
    map.addLayer(markers);
    map.fitBounds(latLng);
}

function normalizeNames(contributor) {
    var name = contributor.name != null ? "<strong>Name:</strong> "+contributor.name +"<br />" : "";
    var description = contributor.description ? "<strong>Description:</strong> "+contributor.description+"<br />" : "";
    var website = (contributor.website !== null) ? "<strong>Website:</strong> <a href=\"+contributor.website\">"+contributor.website+"</a><br />" : "";
    var country = contributor.country.name ? "<strong>Country:</strong> "+contributor.country.name+"<br />" : "";
    return name + description + website + country;
}

function displayContributors(data) {
    var table = $('<table></table>').addClass('table table-bordered table-striped');
    var thead = $('<thead></thead>');
    var tr = $('<tr></tr>');
    var num = $('<th></th>').text('#');
    var name = $('<th></th>').text('Name');
    var desc = $('<th></th>').text('Description');
    var web = $('<th></th>').text('Website');
    var country = $('<th></th>').text('Country');
    table.append(thead);
    thead.append(tr);
    tr.append(num)
    tr.append(name);
    tr.append(desc);
    tr.append(web);
    tr.append(country);
    var tbody = $('<tbody></tbody>');
    var contributors = data.query.results.json.contributors;
    $.each(contributors, function(index,contributor){
        var row = $('<tr></tr>');
        var numData = $('<td></td>').text(index + 1);
        var nameData = $('<td></td>').text(contributor.name);
        var descData = $('<td></td>').text(contributor.description);
        var websiteData = $('<td></td>').text(contributor.website);
        var countryData = $('<td></td>').text(contributor.country.name);
        table.append(tbody);
        tbody.append(row);
        row.append(numData);
        row.append(nameData);
        row.append(descData);
        row.append(websiteData);
        row.append(countryData);
    });
    $('#contrib').append(table);
}
