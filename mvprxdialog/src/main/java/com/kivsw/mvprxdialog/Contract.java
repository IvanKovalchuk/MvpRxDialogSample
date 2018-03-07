package com.kivsw.mvprxdialog;

import io.reactivex.annotations.NonNull;

/**
 * Created by ivan on 8/30/2017.
 */

public interface Contract {
    interface IPresenter
    {
        IView getUI();
        void setUI(@NonNull IView view);
        void removeUI();
        void setPresenterId(long id);
        long getPresenterId();


    }

    interface IDialogPresenter extends IPresenter
    {
        void onCancel();
        void onDismiss();
    }

    interface IView
    {


    }
}
