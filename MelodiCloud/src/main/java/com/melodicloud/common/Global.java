package com.melodicloud.common;

import android.app.Activity;

import de.voidplus.soundcloud.SoundCloud;

/**
 * Created by AhmedSalem on 1/22/15.
 * Contains Global Variables
 */
public class Global {


    public static Activity appContext = null;

    public static SoundCloud soundcloud = new SoundCloud(
            Constants.SoundCloudID,
            Constants.SoundCloudSecret
    );
}
