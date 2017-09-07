package com.kivsw.cloud.OAuth;

import android.net.Uri;

/**
 * Created by ivan on 6/9/17.
 */
// http://api.mail.ru/docs/guides/oauth/standalone/
// http://api.mail.ru/docs/guides/oauth/client-credentials/#refresh_token

public class MailRuOAuthCore
        extends BaseIOAuthCore
{
    static public MailRuOAuthCore newInstance(String clientId, String callbackUri)
    {
        return new MailRuOAuthCore(clientId, callbackUri);
    }


    final private String SCHEME="https",
            AUTHORITY="connect.mail.ru",
            PATH="oauth/authorize";

    private MailRuOAuthCore(String clientId, String callbackUri)
    {

        if(callbackUri==null)
            callbackUri = "https://connect.mail.ru/oauth/success.html"; // https://oauth.yandex.ru/verification_code
        this.callbackUri = Uri.parse(callbackUri);

        this.clientId=clientId;

        // forms URI to get the token
        Uri.Builder builder = new Uri.Builder();

        builder.scheme(SCHEME);
        builder.authority(AUTHORITY);
        builder.appendEncodedPath(PATH);

        builder.appendQueryParameter("response_type","token");
        builder.appendQueryParameter("client_id",clientId);
        builder.appendQueryParameter("redirect_uri",callbackUri);

        builder.appendQueryParameter("display","mobile");


        oauthUri = builder.build();
    }

}
