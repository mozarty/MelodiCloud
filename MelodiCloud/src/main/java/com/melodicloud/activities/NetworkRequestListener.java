package com.melodicloud.activities;

/**
 * Created by AhmedSalem on 6/6/15.
 */
public interface NetworkRequestListener<T> {

    void onRequestFailure(Exception e);

    void onRequestSuccess(T rootServerResponse);
}
