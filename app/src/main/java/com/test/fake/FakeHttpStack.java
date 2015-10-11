package com.test.fake;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


class FakeHttpStack implements HttpStack {
    private static final String DEFAULT_STRING_RESPONSE = "Hello";
    private static final String DEFAULT_JSON_RESPONSE = " {\"a\":1,\"b\":2,\"c\":3}";
    private static final String URL_PREFIX = "http://example.com/";
    private static final String LOGGER_TAG = "STACK_OVER_FLOW";

    private static final int SIMULATED_DELAY_MS = 500;
    private final Context context;

    FakeHttpStack(Context context) {
        this.context = context;
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> stringStringMap)
            throws IOException, AuthFailureError {
        HttpResponse response
                = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
        ArrayList<Header> headers = defaultHeaders();
        response.setHeaders(headers.toArray(new Header[0]));
        response.setLocale(Locale.ENGLISH);
        response.setEntity(createEntity(request));
        return response;
    }

    private ArrayList<Header> defaultHeaders() {
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd mmm yyyy HH:mm:ss zzz");
        Header header1 = new BasicHeader("Date", dateFormat.format(new Date()));
        Header header2 = new BasicHeader("Server",
                        /* Data below is header info of my server */
                "Apache/1.3.42 (Unix) mod_ssl/2.8.31 OpenSSL/0.9.8e");
        ArrayList arrList = new ArrayList<>();
        arrList.add(header1);
        arrList.add(header2);
        return arrList;
    }

    public String readFully(InputStream inputStream, Charset charset)
            throws IOException {
        return new String(readFully(inputStream), "UTF-8");
    }

    private byte[] readFully(InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    /**
     * returns the fake content using resource file in res/raw. fake_res_foo.txt is used for
     * request to http://example.com/foo
     */
    private HttpEntity createEntity(Request request) throws UnsupportedEncodingException {
        String resourceName = constructFakeResponseFileName(request);
        int resourceId = context.getResources().getIdentifier(
                resourceName, "raw", context.getApplicationContext().getPackageName());
        if (resourceId == 0) {
            Log.w(LOGGER_TAG, "No fake file named " + resourceName
                    + " found. default fake response should be used.");
        } else {
            InputStream stream = context.getResources().openRawResource(resourceId);
            try {
                if (resourceName.contains("raw")) {
                    return new ByteArrayEntity(readFully(stream));
                } else {
                    String string = readFully(stream, null);
                    return new StringEntity(string);
                }
            } catch (IOException e) {
                Log.e(LOGGER_TAG, "error reading " + resourceName, e);
            }
            catch (Exception e)
            {
                Log.e(LOGGER_TAG, "error reading " + resourceName, e);
            }
        }

        // Return default value since no fake file exists for given URL.
        if (request instanceof StringRequest) {
            return new StringEntity(DEFAULT_STRING_RESPONSE);
        }
        return new StringEntity(DEFAULT_JSON_RESPONSE);
    }

    /**
     * Map request URL to fake file name
     */
    private String constructFakeResponseFileName(Request request) {
        String reqUrl = request.getUrl();
        String fileName = reqUrl.substring(reqUrl.lastIndexOf('/') + 1, reqUrl.length());
        if (fileName.contains("?"))
            fileName = fileName.substring(0, fileName.lastIndexOf('?'));
        else
            fileName = fileName.substring(0, fileName.length());

        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }
}