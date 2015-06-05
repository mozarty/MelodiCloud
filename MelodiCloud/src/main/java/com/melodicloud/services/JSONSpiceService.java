package com.melodicloud.services;

import android.app.Application;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.gson.GsonObjectPersisterFactory;
import com.melodicloud.common.Constants;

import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * This service will handle the calls to the backend using the implemented requests
 */
public class JSONSpiceService extends SpringAndroidSpiceService {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Override
    public CacheManager createCacheManager(Application application)
            throws CacheCreationException {

        CacheManager cacheManager = new CacheManager();
        GsonObjectPersisterFactory gsonObjectPersisterFactory = new GsonObjectPersisterFactory(
                application);
        cacheManager.addPersister(gsonObjectPersisterFactory);
        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return Constants.SERVICE_THREAD_COUNT;
    }


    /**
     * this method creates the Rest template that will be used for all web services
     * <p/>
     * the current implementation uses Gson for parsing
     * <p/>
     * find more complete examples in RoboSpice Motivation app
     * to enable Gzip compression and setting request timeouts.
     * web services support json responses
     */
    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        AbstractHttpMessageConverter jsonConverter;
        if (Constants.DEBUG_JSON) {
            jsonConverter = new GsonHttpDebugMessageConverter();
        } else {
            jsonConverter = new GsonHttpMessageConverter();
        }

        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate
                .getMessageConverters();

        configureJsonConverter(jsonConverter);

        listHttpMessageConverters.add(jsonConverter);
        listHttpMessageConverters.add(formHttpMessageConverter);
        listHttpMessageConverters.add(stringHttpMessageConverter);
        restTemplate.setMessageConverters(listHttpMessageConverters);


        return restTemplate;
    }

    /**
     * adds more file types to be consedered as JSON in cae the back-end doesn't return the type as Application/json
     * and updates the configured Gson instance
     *
     * @param jsonConverter
     */
    private void configureJsonConverter(AbstractHttpMessageConverter jsonConverter) {
        List<MediaType> suppMediaTypes = new ArrayList<MediaType>();

        suppMediaTypes.add(new MediaType("text", "json", DEFAULT_CHARSET));
        suppMediaTypes.add(new MediaType("text", "*+json", DEFAULT_CHARSET));
        suppMediaTypes
                .add(new MediaType("application", "json", DEFAULT_CHARSET));
        suppMediaTypes.add(new MediaType("application", "*+json",
                DEFAULT_CHARSET));

        jsonConverter.setSupportedMediaTypes(suppMediaTypes);


        if (Constants.DEBUG_JSON) {
            ((GsonHttpDebugMessageConverter) jsonConverter).setGson(getGson().create());
        } else {
            ((GsonHttpMessageConverter) jsonConverter).setGson(getGson().create());
        }


    }

    /**
     * Configures Gson Library to consider the @Expose annotation while serializing & deserializing
     *
     * @return
     */
    public static GsonBuilder getGson() {
        return new GsonBuilder().addSerializationExclusionStrategy(
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(
                            FieldAttributes fieldAttributes) {
                        final Expose expose = fieldAttributes
                                .getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                }).addDeserializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                final Expose expose = fieldAttributes
                        .getAnnotation(Expose.class);
                return expose != null && !expose.deserialize();
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        });
    }
}