package com.kivsw.cloud.disk.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds all the tokens in a SharedPreferences
 */

public class TokenKeeper {

    static Map<String, TokenKeeper> map = new HashMap();
    public synchronized static TokenKeeper getInstance(Context context, String name)
    {
        TokenKeeper res=map.get(name);
        if(res==null)
        {
            res = new TokenKeeper(context, name);
            map.put(name, res);
        }
        return res;
    }


    private SharedPreferences sharedPreferences;


    private TokenKeeper(Context context, String name)
    {
        sharedPreferences =  context.getSharedPreferences(name,Context.MODE_PRIVATE);
    };



    final String TOKEN="token";
    public String getToken()
    {
        String token= sharedPreferences.getString(TOKEN,null);
        return token;
    }
    public void setToken(String val)
    {
        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putString(TOKEN, val);
        ed.remove(REFRESH_TOKEN);
        ed.remove(EXPIRED_TIME);
        ed.commit();
    };

    final String REFRESH_TOKEN="refresh_token";
    public String getRefreshToken()
    {
        return sharedPreferences.getString(REFRESH_TOKEN,null);
    }
    public void setRefreshToken(String val)
    {
        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putString(REFRESH_TOKEN, val);
        ed.commit();
    };

    final String EXPIRED_TIME="expired_time";
    public long getExpiredTime()
    {
        return sharedPreferences.getLong(EXPIRED_TIME,0);
    }
    public void setExpiredIn(long val)
    {
        long currentTime=System.currentTimeMillis();

        SharedPreferences.Editor ed=sharedPreferences.edit();
        ed.putLong(EXPIRED_TIME, currentTime + val);
        ed.commit();
    };

}
