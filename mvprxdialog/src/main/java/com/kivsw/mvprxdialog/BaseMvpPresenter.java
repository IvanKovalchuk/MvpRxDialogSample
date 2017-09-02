package com.kivsw.mvprxdialog;

import android.support.v4.app.Fragment;

/**
 * Created by ivan on 9/2/2017.
 */

public abstract class BaseMvpPresenter
        implements Contract.IPresenter
{
    long presenterId=0;


    @Override
    public void setPresenterId(long id) {
        presenterId = id;
    }

    @Override
    public long getPresenterId() {
        return presenterId;
    }

    @Override
    public void onCancel() {
        deletePresenter();
    }

    @Override
    public void onDismiss() {
        deletePresenter();
    }

    protected void deletePresenter()
    {
            PresenterManager.getInstance().deletePresenter(getPresenterId());

    }
}
