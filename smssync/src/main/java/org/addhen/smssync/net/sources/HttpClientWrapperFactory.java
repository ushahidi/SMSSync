package org.addhen.smssync.net.sources;


import android.os.Build;

public class HttpClientWrapperFactory {

    public HttpClientWrapper create(String url, int timeout, String userAgent) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            return new LegacyHttpClientWrapper(url, timeout, userAgent);
        }
        return new OkHttpClientWrapper(url, timeout, userAgent);
    }
}
