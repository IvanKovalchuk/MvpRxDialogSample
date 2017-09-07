package com.kivsw.cloud.OAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OAuthActivity extends AppCompatActivity
        implements OAuthActivityContract.IView{

    WebView webView;

    OAuthPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_oauth);
        webView = new WebView(this);
        setContentView(webView);

        presenter = OAuthPresenter.getInstance(this);

        //webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                processResult(url);
                return false; //Allow WebView to load url
            }
        });

        if(savedInstanceState==null) {

        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(presenter.getOAuthURL());
    }


    @Override
    protected void onStart() {
        super.onStart();

        presenter.setUI(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.setUI(null);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected void processResult(String urlStr)
    {
        presenter.processRedirection(urlStr);

    }





    static public void showActivity(Context context)
    {
        //Intent i=new Intent("com.kovalchuk.ivan.testoauth.START_ACTION");

        Intent i=new Intent(context, OAuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    public void close() {
        /*CookieManager cm= CookieManager.getInstance();
        boolean b=cm.acceptCookie();
        cm.removeAllCookie();*/
        finish();
    }
}