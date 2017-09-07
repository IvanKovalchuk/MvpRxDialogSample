package com.kivsw.cloud.OAuth;

import android.net.Uri;

/**
 * Created by ivan on 6/7/17.
 */
// https://tech.yandex.ru/oauth/doc/dg/reference/mobile-client-docpage/

public class YandexOAuthCore
        extends BaseIOAuthCore
{

    static public YandexOAuthCore newInstance(String clientId, String callbackUri)
    {
        return newInstance(clientId, callbackUri, null, null);
    }
    static public YandexOAuthCore newInstance(String clientId, String callbackUri, String deviceId, String deviceName)
    {
        YandexOAuthCore res = new YandexOAuthCore(clientId, callbackUri, deviceId, deviceName);

        return res;
    }

    final private String SCHEME="https",
                         AUTHORITY="oauth.yandex.ru",
                         PATH="authorize";

    private String clientId, deviceId, deviceName;

    private YandexOAuthCore(String clientId, String callbackUri, String deviceId, String deviceName)
    {
//String surl = String.format("https://oauth.yandex.ru/authorize?response_type=code&client_id=%s&device_id=%s&device_name=%s&force_confirm=yes", clientId, devId, devName);


        if(callbackUri==null)
            callbackUri = "https://oauth.yandex.ru/verification_code"; // https://oauth.yandex.ru/verification_code
        this.callbackUri = Uri.parse(callbackUri);

        this.clientId=clientId;
        this.deviceId=deviceId;
        this.deviceName=deviceName;

        prepareUri();
    }



    public void prepareUri()
    {
        // forms URI to get the token
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SCHEME);
        builder.authority(AUTHORITY);
        builder.appendPath(PATH);

        builder.appendQueryParameter("response_type","token");
        builder.appendQueryParameter("client_id",clientId);

        if(deviceId!=null)
            builder.appendQueryParameter("device_id",deviceId);

        if(deviceName!=null)
            builder.appendQueryParameter("device_name",deviceName);

        builder.appendQueryParameter("display","popup");
        builder.appendQueryParameter("force_confirm","true");

        oauthUri = builder.build();
    };


}
