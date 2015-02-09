package org.addhen.smssync.net.sources;

import org.addhen.smssync.net.HttpMediaType;
import org.addhen.smssync.net.HttpMethod;
import org.addhen.smssync.net.MainHttpClient;
import org.addhen.smssync.net.ssl.TrustedSocketFactory;
import org.addhen.smssync.util.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class LegacyHttpClientWrapper implements HttpClientWrapper {

    private static final String CLASS_TAG = MainHttpClient.class.getSimpleName();

    private static final String DEFAULT_ENCODING = "UTF-8";

    private DefaultHttpClient httpClient;

    private String url;

    private HttpParams httpParameters;

    private ArrayList<NameValuePair> params;

    private Map<String, String> headers;

    private HttpEntity entity;

    private HttpResponse httpResponse;

    private HttpRequestBase request;

    private HttpMethod method = HttpMethod.GET;

    private int responseCode;

    private StringEntity stringEntity;

    private String response;

    private String responseErrorMessage;

    public LegacyHttpClientWrapper(String url, int timeout) {
        this.url = url;
        this.params = new ArrayList<>();
        this.headers = new HashMap<>();
        this.method = HttpMethod.GET;

        request = new HttpGet(url);

        httpParameters = new BasicHttpParams();
        httpParameters.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        httpParameters.setParameter(
                ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE,
                new ConnPerRouteBean(1));

        httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE,
                false);

        HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParameters, "utf8");

        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);

        HttpConnectionParams.setSoTimeout(httpParameters, timeout);

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80));
        // https scheme
        try {
            schemeRegistry.register(new Scheme("https",
                    new TrustedSocketFactory(url, false), 443));
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
                httpParameters, schemeRegistry);

        httpClient = new DefaultHttpClient(manager, httpParameters);

    }

    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 1024);
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            debug(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                debug(e);
            }
        }
        return sb.toString();
    }

    @Override
    public void debug(Exception e) {
        Logger.log(CLASS_TAG, "Exception: "
                + e.getClass().getName()
                + " " + e.getMessage());
    }

    private String getQueryString() throws Exception {
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
    public void setHeader(String name, String value) {
        headers.put(name, value);
        request.setHeader(name, value);
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
    public void setRequestBody(HttpMediaType mediaType, ArrayList<NameValuePair> body) throws Exception {
        setEntity(new UrlEncodedFormEntity(body, HTTP.UTF_8));
    }

    @Override
    public void setRequestBody(HttpMediaType mediaType, String body) throws Exception {
        setStringEntity(body);
    }

    @Override
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    private void prepareRequest() throws Exception {

        if (method.equals(HttpMethod.GET)) {
            request = new HttpGet(url + getQueryString());

        } else if (method.equals(HttpMethod.POST)) {
            request = new HttpPost(url);
            if (getEntity() != null) {
                ((HttpPost) request).setEntity(getEntity());
            } else if (getStringEntity() != null) {
                ((HttpPost) request).setEntity(getStringEntity());
            }

        } else if (method.equals(HttpMethod.PUT)) {
            request = new HttpPut(url);

            if (getEntity() != null) {
                ((HttpPut) request).setEntity(getEntity());
            } else if (getStringEntity() != null) {
                ((HttpPut) request).setEntity(getStringEntity());
            }

        }
        // set headers on request
        for (String key : headers.keySet()) {
            request.setHeader(key, headers.get(key));
        }
    }

    private HttpEntity getEntity() throws Exception {
        // check if entity was explicitly set otherwise return params as entity
        if (entity != null && entity.getContentLength() > 0) {
            return entity;
        } else if (!params.isEmpty()) {
            // construct entity if not already set
            return new UrlEncodedFormEntity(params, DEFAULT_ENCODING);
        }
        return null;
    }

    private void setEntity(HttpEntity data) throws Exception {
        entity = data;
    }

    public StringEntity getStringEntity() throws Exception {
        // check if entity was explicitly set otherwise return params as entity
        if (stringEntity != null && stringEntity.getContentLength() > 0) {
            return stringEntity;
        } else if (!params.isEmpty()) {
            // construct entity if not already set
            return new UrlEncodedFormEntity(params, DEFAULT_ENCODING);
        }
        return null;
    }

    public void setStringEntity(String data) throws Exception {
        stringEntity = new StringEntity(data, DEFAULT_ENCODING);
    }

    private HttpRequestBase getRequest() throws Exception {
        prepareRequest();
        return request;
    }

    public void execute() throws Exception {

        try {
            httpResponse = httpClient.execute(getRequest());
            responseCode = httpResponse.getStatusLine().getStatusCode();
            responseErrorMessage = httpResponse.getStatusLine().getReasonPhrase();
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();

                response = convertStreamToString(instream);
                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (ClientProtocolException e) {
            httpClient.getConnectionManager().shutdown();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            httpClient.getConnectionManager().shutdown();
            throw e;
        }
    }
}
