package com.melodicloud.services.requests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melodicloud.common.Global;
import com.melodicloud.util.LogUtil;
import com.octo.android.robospice.SpiceManager;

import java.util.ArrayList;

import de.voidplus.soundcloud.Track;

/**
 * Created by AhmedSalem on 1/25/15.
 */
public class LoadSongsRequest extends BaseRequest<ArrayList<Track>> {

    SpiceManager spiceManager;
    boolean forceRefresh = false;

    public LoadSongsRequest(SpiceManager spiceManager, boolean forceRefresh) {
        super((Class<ArrayList<Track>>) (Class<?>) ArrayList.class);
        this.spiceManager = spiceManager;
        this.forceRefresh = forceRefresh;
    }


    @Override
    public ArrayList<Track> loadDataFromNetwork() throws Exception {

        ArrayList<Track> songs;

        songs = getObjectFromJson(spiceManager.getDataFromCache(String.class, createCacheKey()).get());

        if (songs == null || forceRefresh) {
            songs = new ArrayList<>();

            LogUtil.i("loading", "starting loading");
            int offset = 0;
            while (true) {
                ArrayList<Track> songsPage = Global.soundcloud.getMeFavorites(offset, 200);

                if (songsPage == null || songsPage.isEmpty()) {

                    break;
                }
                LogUtil.i("loading", "loaded " + songsPage.size());

                songs.addAll(songsPage);

                LogUtil.i("loading", "all is  " + songs.size());

                offset += songsPage.size();


                LogUtil.i("loading", "offset is  " + offset);

                break;
            }
            spiceManager.putDataInCache(createCacheKey(), getJsonFromObject(songs));
        }

        return songs;
    }

    protected ArrayList<Track> getObjectFromJson(String json) {
        Gson gson = new Gson();
        ArrayList<Track> result = gson.fromJson(json, new TypeToken<ArrayList<Track>>() {
        }.getType());

        return result;
    }

    @Override
    public String createCacheKey() {
        return "getFavourites";
    }
}
