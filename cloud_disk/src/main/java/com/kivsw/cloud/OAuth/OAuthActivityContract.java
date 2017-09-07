package com.kivsw.cloud.OAuth;

import io.reactivex.Observable;

/**
 * Created by ivan on 6/7/17.
 */

public interface OAuthActivityContract {


    /**
     * startAuthorization core, processes redirection
     */
    interface IOAuthCore {
        String getOAuthURL();  // gets startAuthorization url

        boolean processRedirection(String url); // returns true if startAuthorization finished
        Observable<IOAuthCore> getObservable();
    }

    class IOAuthException extends Exception{
        public IOAuthException() {
            super();
        }
        public IOAuthException(String message) {
            super(message);
        }
        public IOAuthException(String message, Throwable cause) {
            super(message, cause);
        }
    };

    /**
     * presenter
     */
    interface IPresenter
    {
        void setUI(IView view);

        String getOAuthURL();
        void processRedirection(String url);

        Observable<IOAuthCore> startAuthorization(IOAuthCore core);

    }


    /**
     * activity
     */
    interface IView
    {
        void close();
    }
}
