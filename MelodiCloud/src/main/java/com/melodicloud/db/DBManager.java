package com.melodicloud.db;

/**
 * Created by AhmedSalem on 1/22/15.
 */
public class DBManager {
    private static DBManager ourInstance = new DBManager();

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
    }
}
