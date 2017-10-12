package com.ntsoft.ihhq.model;

/**
 * Created by Administrator on 7/21/2017.
 */

public class Global {
    public static Global instance;
    public static Global getInstance() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }
    public UserModel me = new UserModel();

    public UserModel getMe() {
        return me;
    }

    public void setMe(UserModel me) {
        this.me = me;
    }
}
