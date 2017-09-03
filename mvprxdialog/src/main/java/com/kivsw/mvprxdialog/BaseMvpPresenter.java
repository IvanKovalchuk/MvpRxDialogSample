package com.kivsw.mvprxdialog;

/**
 * Created by ivan on 9/2/2017.
 */

public abstract class BaseMvpPresenter
        implements Contract.IDialogPresenter
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
