package com.melodicloud.services.requests;

import com.google.gson.Gson;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * This Base Request contatins the common methods used by all requests like adding Headers
 *
 * @param <T>
 */
public abstract class BaseRequest<T> extends SpringAndroidSpiceRequest<T> {

    public BaseRequest(Class<T> clazz) {
        super(clazz);
    }

    protected HttpHeaders getDefaultHeaders() {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        // Add More Application Headers
        requestHeaders.add("Accept", "application/json");

        return requestHeaders;
    }

    protected String getJsonFromObject(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }



    /**
     * This method generates a unique cache key for this request. In this case
     * our cache key depends just on the keyword.
     *
     * @return
     */
    public abstract String createCacheKey();
}
