package com.melodicloud;

import android.app.Application;

import com.melodicloud.services.DownloadService;
import com.melodicloud.util.PrefrencesUtil;

/**
 * Created by AhmedSalem on 1/22/15.
 */
public class MelodiCloudApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());
        PrefrencesUtil.init(this);
        DownloadService.init(this);
    }

}
