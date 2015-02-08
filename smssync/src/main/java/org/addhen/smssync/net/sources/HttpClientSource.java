package org.addhen.smssync.net.sources;

import java.io.Reader;
import java.util.concurrent.TimeUnit;

/**
 * Http client source
 */
public interface HttpClientSource {

    void setHeader(String name, String value);

    int responseCode();

    String getResponseBody();

    String getErrorMessage();

    void setRequestParameter();

    void execute() throws Exception;

    void debug(Exception e);

}
