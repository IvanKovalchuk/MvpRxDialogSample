package com.kivsw.cloud.OAuth;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * Created by ivan on 6/15/17.
 */

public abstract class BaseIOAuthCore
        implements OAuthActivityContract.IOAuthCore
{
    protected Uri callbackUri, oauthUri;
    protected String clientId; // id of this app

    protected Subject observable;

    private Map<String, String> paremeters=new HashMap();


    public BaseIOAuthCore()
    {
        observable= PublishSubject.create();
    }
    @Override
    public String getOAuthURL() {
        return oauthUri.toString();
    }

    @Override
    public boolean processRedirection(String redirect_uri) {
        Uri uri=Uri.parse(redirect_uri.replace('#','?'));

        if(uri.getAuthority().equals( callbackUri.getAuthority()) &&
                uri.getPath().equals( callbackUri.getPath())  )
        {
            // all parameters
            paremeters.clear();
            Set<String> names=uri.getQueryParameterNames();
            for(String name:names)
                paremeters.put(name, uri.getQueryParameter(name) );

            observable.onNext(this);
            return true;
        }
        return false;
    }

    @Override
    public Observable<OAuthActivityContract.IOAuthCore> getObservable()
    {
        return observable;
    };

    // access token
    public String getToken() {
        return getParam("access_token");
    }

    public String getRefreshToken() {
        return getParam("refresh_token");
    }

    public String getExpiredIn() {
        return getParam("expires_in");
    }

    // error
    public String getErrorDesc() {
        return getParam("error_description");
    }

    public String getErrCode() {
        return getParam("error");
    }

    public String getParam(String name)
    {
        return paremeters.get(name);
    }

}

