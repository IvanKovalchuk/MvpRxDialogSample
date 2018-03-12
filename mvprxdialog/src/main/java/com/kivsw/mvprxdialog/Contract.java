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

    }

    interface IDialogPresenter extends IPresenter
    {
        void registerDialogPresenter();
        long getDialogPresenterId();

        void onCancel();
        void onDismiss();
    }

    interface IView
    {


    }
}
