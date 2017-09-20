package com.kivsw.cloud.OAuth;

import android.content.Context;
import android.webkit.CookieManager;

import io.reactivex.Observable;

/**
 * Created by ivan on 6/7/17.
 */

public class OAuthPresenter implements OAuthActivityContract.IPresenter {


    static OAuthPresenter singletone=null;
    static public OAuthPresenter getInstance(Context cnt)
    {
        if(singletone==null)
            singletone = new OAuthPresenter(cnt);
        return singletone;
    }

    Context context;
    OAuthActivityContract.IView view;
    OAuthActivityContract.IOAuthCore core;
    private OAuthPresenter(Context cnt)
    {
        context = cnt.getApplicationContext();
    };

    @Override
    public void setUI(OAuthActivityContract.IView view) {
        this.view =  view;
    }

    @Override
    public String getOAuthURL() {
        return core.getOAuthURL();
    }

    @Override
    public void processRedirection(String url) {
        if(core.processRedirection(url)) {
            if(view!=null)
                view.close();

        }
    }

    @Override
    public void cancelOAuth() {
        core.cancel();
    }

    @Override
    public Observable<OAuthActivityContract.IOAuthCore> startAuthorization(OAuthActivityContract.IOAuthCore core) {
        this.core=core;

        //
        CookieManager cm= CookieManager.getInstance();
        boolean b=cm.acceptCookie();
        cm.removeAllCookie();


        OAuthActivity.showActivity(context);

        return core.getObservable();

    }


}
