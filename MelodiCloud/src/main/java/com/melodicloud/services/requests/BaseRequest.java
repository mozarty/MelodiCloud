package com.melodicloud.services.requests;


import android.os.AsyncTask;

import com.google.gson.Gson;
import com.melodicloud.activities.NetworkRequestListener;

/**
 * This Base Request contatins the common methods used by all requests like adding Headers
 *
 * @param <T>
 */
public abstract class BaseRequest<T> extends AsyncTask<Object, Void, T> {


    protected String getJsonFromObject(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    public abstract T loadDataFromNetwork() throws Exception;

    @Override
    protected T doInBackground(Object... objects) {
        NetworkRequestListener listener = (NetworkRequestListener) objects[0];
        T result = null;
        try {
            result = loadDataFromNetwork();
            if (result == null)
                listener.onRequestFailure(new NullPointerException());
            else
                listener.onRequestSuccess(result);
        } catch (Exception e) {
            listener.onRequestFailure(e);
        }

        return result;
    }

    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     *
     * @return
     */
    public abstract String createCacheKey();
}
