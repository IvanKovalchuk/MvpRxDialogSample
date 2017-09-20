package com.kivsw.cloudcache;

import android.content.Context;

/**
 * Created by ivan on 9/19/17.
 */

public class CloudCache {

    static CloudCache singletone=null;

    public static synchronized  CloudCache getInstance(Context context) {

        if(singletone == null)
        {
            String dir = context.getCacheDir().getAbsolutePath();
            String exDir=  context.getCacheDir().getAbsolutePath();
            singletone=new CloudCache();

        }

        return singletone;
    }
}
