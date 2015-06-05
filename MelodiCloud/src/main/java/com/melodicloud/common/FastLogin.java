package com.melodicloud.common;

/**
 * Created by AhmedSalem on 1/25/15.
 */
public class FastLogin {

    public static boolean ENABLED = true && Constants.DEBUG;

    public static Account account = Account.Test1;

    public enum Account {
        Test1("ahmed_hassan_salem@hotmail.com", "111989");


        String username;

        String password;

        Account(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }
    }
}
