package com.melodicloud.util;

import android.content.Context;

import net.orange_box.storebox.StoreBox;
import net.orange_box.storebox.annotations.method.KeyByString;

/**
 * Created by Salem on 29-Apr-15.
 */
public class PrefrencesUtil {

    private static Context context;

    public interface MyPreferences {

        @KeyByString("key_username")
        String getUsername();

        @KeyByString("key_password")
        String getPassword();

        @KeyByString("key_username")
        void setUsername(String username);

        @KeyByString("key_password")
        void setPassword(String username);
    }

    public static void init(Context context) {
        PrefrencesUtil.context = context;
        prefrences = StoreBox.create(context, MyPreferences.class);
    }

    public static MyPreferences prefrences;
}
