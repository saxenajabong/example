package com.test.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ListRequest extends JsonRequest<ArrayList<ListModel>> {

    /**
     * Creates a new request.
     *
     * @param method        the HTTP method to use
     * @param url           URL to fetch the JSON from
     * @param jsonRequest   A {@link JSONObject} to post with the request. Null is allowed and
     *                      indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the ArrayList<ListModel>
     * @param errorListener Error listener, or null to ignore errors.
     */
    public ListRequest(int method, String url, JSONObject jsonRequest,
                       Response.Listener<ArrayList<ListModel>> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);

    }

    /**
     * Creates a new request.
     *
     * @param listener      Listener to receive the ArrayList<ListModel>
     * @param errorListener Error listener, or null to ignore errors.
     */
    public ListRequest(String url, Response.Listener<ArrayList<ListModel>> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, null, listener,
                errorListener);

    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     * @see #ListRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)
     */
    public ListRequest(String url, JSONObject jsonRequest, Response.Listener<ArrayList<ListModel>> listener,
                       Response.ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<ArrayList<ListModel>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject jsonObject = new JSONObject(jsonString);
            return Response.success(ListModel.cast(jsonObject.getJSONArray("items")),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
