package com.kivsw.mvprxdialog;

/**
 * Created by ivan on 8/30/2017.
 */

public interface Contract {
    interface IPresenter
    {
        IView getUI();
        void setUI(IView view);
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
