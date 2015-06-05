package com.melodicloud.common;

import com.melodicloud.BuildConfig;

/**
 * Created by AhmedSalem on 1/22/15.
 * This class defines the Constants used by the whole application
 */
public class Constants {
    public static final boolean DEBUG = BuildConfig.DEBUG;

    //TODO Security Risk, I should hide these values somehow
    public static final String SoundCloudID = "ca8d2bfe9f594292bb2458243b7b9354";
    public static final String SoundCloudSecret = "01b30c05e727ffb1321961358e207711";

    public static final int SERVICE_THREAD_COUNT = 5;

    //Switch this on to enable logging the json before being parsed to trace parsing errors
    public static final boolean DEBUG_JSON = false;


}
