package com.top.arch;

import android.app.Application;

import com.top.arch.net.HttpManager;
import com.top.arch.net.HttpsClient;

public class Top {

    private static Application instance;

    public static void init(Application app) {
        if (instance == null) {
            instance = app;
        }
    }



    public static HttpManager getNet(){
        return HttpsClient.getInstance();
    }


    public static void getUtils(){


    }


}
