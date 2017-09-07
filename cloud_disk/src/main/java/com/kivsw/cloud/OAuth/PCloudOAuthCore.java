package com.kivsw.cloud.OAuth;

import android.net.Uri;

/**
 * Created by ivan on 6/22/17.
 */
//https://docs.pcloud.com/methods/oauth_2.0/authorize.html
public class PCloudOAuthCore
        extends BaseIOAuthCore
{

    static public PCloudOAuthCore newInstance(String clientId, String callbackUri)
    {
        PCloudOAuthCore res = new PCloudOAuthCore(clientId, callbackUri);

        return res;
    }

    final private String SCHEME="https",
            AUTHORITY="my.pcloud.com",
            PATH="oauth2/authorize";

    private PCloudOAuthCore(String clientId, String callbackUri)
    {
       if(callbackUri==null)
            callbackUri = "localhost://oauth";
        this.callbackUri = Uri.parse(callbackUri);

        this.clientId=clientId;

        // forms URI to get the token
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SCHEME);
        builder.authority(AUTHORITY);
        builder.appendEncodedPath(PATH);

        builder.appendQueryParameter("response_type","token");
        builder.appendQueryParameter("client_id",clientId);
        if(callbackUri!=null)
             builder.appendQueryParameter("redirect_uri",callbackUri);

        oauthUri = builder.build();

    }

}
