
var url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20json%20where%20url%3D%22https%3A%2F%2Fraw.githubusercontent.com%2Fushahidi%2FSMSSync%2Fmaster%2Fcontributors.json%3F_out%3Djson%22%20&format=json";
$.ajax({
    url: url,
    cache : false,
    dataType : 'json',
    success : fetchContributors
});

// Get contributors details
function fetchContributors(data) {
    var titleLayer = L.tileLayer('http://otile{s}.mqcdn.com/tiles/1.0.0/map/{z}/{x}/{y}.png', {attribution: 'Map data &copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors, Imagery &copy; <a href="http://info.mapquest.com/terms-of-use/">MapQuest</a>', subdomains: '1234'});
    var map = L.map('map');
    titleLayer.addTo(map);
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

    // Put data in a table.
    displayContributors(data);
}

function normalizeNames(contributor) {
    var name = contributor.name != null ? '<strong>Name:</strong> '+contributor.name +'<br /><br />' : '';
    var description = contributor.description ? '<strong>Description:</strong> '+contributor.description+'<br /><br />' : '';
    var website = '<strong>Webste:</strong> '+normalizeWebsite(contributor.website)+ '<br /><br />';
    var country = contributor.country.name ? '<strong>Country:</strong> '+contributor.country.name+'<br /><br />' : '';
    return name + description + website + country;
}

function normalizeWebsite(website) {
    return (website !== 'null') ? '<a href="'+website+'">'+website+'</a>' : 'No website address provided';
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
        var website = normalizeWebsite(contributor.website);
        var websiteData = $('<td></td>').html(website);
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
