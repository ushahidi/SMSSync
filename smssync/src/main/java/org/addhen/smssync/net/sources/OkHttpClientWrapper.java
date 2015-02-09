package org.addhen.smssync.net.sources;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.addhen.smssync.net.HttpMediaType;
import org.addhen.smssync.net.HttpMethod;
import org.addhen.smssync.util.Logger;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Base64;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class OkHttpClientWrapper implements HttpClientWrapper {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final String CLASS_TAG = OkHttpClientWrapper.class.getSimpleName();

    private OkHttpClient httpClient;

    private String url;

    private ArrayList<NameValuePair> params;

    private Map<String, String> header;

    private Headers headers;

    private HttpMethod method = HttpMethod.GET;

    private RequestBody requestBody;

    private int responseCode;

    private String response;

    private String responseErrorMessage;

    private Request request;


    public OkHttpClientWrapper(String url, int timeout, String userAgent) {
        this.url = url;
        this.params = new ArrayList<>();
        this.header = new HashMap<>();

        httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
        httpClient.setWriteTimeout(timeout, TimeUnit.SECONDS);
        httpClient.setReadTimeout(timeout, TimeUnit.SECONDS);
        setHeader("User-Agent", userAgent);
    }

    public static String base64Encode(String str) {
        byte[] bytes = str.getBytes();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    @Override
    public void setHeader(String name, String value) {
        this.header.put(name, value);
        addHeader();
    }

    private void setHeaders(Headers headers) {
        this.headers = headers;
    }

    private void addHeader() {
        try {
            URI uri = new URI(url);
            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                setHeader("Authorization", "Basic " + base64Encode(userInfo));
            }
        } catch (URISyntaxException e) {
            debug(e);
        }
        // set headers on request
        Headers.Builder headerBuilder = new Headers.Builder();
        for (String key : header.keySet()) {
            headerBuilder.set(key, header.get(key));
        }

        setHeaders(headerBuilder.build());
    }

    @Override
    public int responseCode() {
        return responseCode;
    }

    @Override
    public String getResponseBody() {
        return response;
    }

    @Override
    public String getErrorMessage() {
        return responseErrorMessage;
    }

    @Override
    public void setRequestParameter(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    @Override
    public ArrayList getParams() {
        return params;
    }

    @Override
    public void setRequestBody(HttpMediaType mediaType, ArrayList<NameValuePair> body)
            throws Exception {
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        List<NameValuePair> params = getParams();
        for (NameValuePair pair : params) {
            formEncodingBuilder.add(pair.getName(), pair.getValue());
        }
        requestBody = formEncodingBuilder.build();

    }

    @Override
    public void setRequestBody(HttpMediaType mediaType, String body) throws Exception {
        MediaType type = setMediaType(mediaType);
        requestBody = RequestBody.create(type, body);
    }

    private MediaType setMediaType(HttpMediaType mediaType) {
        MediaType type;

        switch (mediaType) {

            case JSON:
                type = MediaType.parse("application/json; charset=" + DEFAULT_ENCODING);
                break;

            case XML:
                type = MediaType.parse("application/json; charset=" + DEFAULT_ENCODING);
                break;

            case YAML:
                type = MediaType.parse("application/xml; charset=" + DEFAULT_ENCODING);
                break;

            default:
                type = MediaType.parse("text/plain; charset=" + DEFAULT_ENCODING);

        }

        return type;
    }

    @Override
    public void execute() throws Exception {
        prepareRequest();
        if (request != null) {
            final Response response = httpClient.newCall(request).execute();
            responseCode = response.code();
            responseErrorMessage = response.message();

        }
    }

    private void prepareRequest() throws Exception {
        addHeader();
        // setup parameters on request
        if (method.equals(HttpMethod.GET)) {
            request = new Request.Builder()
                    .url(url + getQueryString())
                    .headers(headers)
                    .build();
        } else if (method.equals(HttpMethod.POST)) {
            request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(requestBody)
                    .build();

        } else if (method.equals(HttpMethod.PUT)) {
            request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .put(requestBody)
                    .build();
        }
    }

    private String getQueryString() throws Exception {
        //add query parameters
        String combinedParams = "";
        if (!params.isEmpty()) {
            combinedParams += "?";
            for (NameValuePair p : params) {
                String paramString = p.getName() + "=" + URLEncoder
                        .encode(p.getValue(), DEFAULT_ENCODING);
                if (combinedParams.length() > 1) {
                    combinedParams += "&" + paramString;
                } else {
                    combinedParams += paramString;
                }
            }
        }
        return combinedParams;
    }

    @Override
    public void debug(Exception e) {
        Logger.log(CLASS_TAG, "Exception: "
                + e.getClass().getName()
                + " " + e.getMessage());
    }
}
