package com.kivsw.mvprxdialog;

/**
 * Created by ivan on 9/2/2017.
 */

public abstract class BaseMvpPresenter
        implements Contract.IDialogPresenter
{
    long presenterId=0;


    @Override
    public void registerDialogPresenter()
    {
        presenterId = PresenterList.getInstance().addNewPresenter(this);
    };
    /*public void setPresenterId(long id) {
        presenterId = id;
    }*/

    @Override
    public long getDialogPresenterId() {
        if(presenterId==0)
            throw new RuntimeException("Presenter is not registered. Invoke registerDialogPresenter().");
        return presenterId;
    }

    @Override
    public void onCancel() {
        deletePresenter();
    }

    @Override
    public void onDismiss() {

       // deletePresenter();
    }

    protected void deletePresenter()
    {
            PresenterList.getInstance().deletePresenter(getDialogPresenterId());
    }
}
