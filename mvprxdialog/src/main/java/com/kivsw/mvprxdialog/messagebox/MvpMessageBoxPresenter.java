package com.kivsw.mvprxdialog.messagebox;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;

import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by ivan on 9/2/2017.
 */

public class MvpMessageBoxPresenter extends BaseMvpPresenter
{
    private MvpMessageBoxPresenter()
    {
        registerDialogPresenter();
    };

    MvpMessageBox view;
    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(@NonNull Contract.IView view) {
        this.view = (MvpMessageBox)view;
        if(needToClose)
            cancelMessageBox();
    }

    @Override
    public void removeUI()
    {
        this.view = null;
    };


    public static final int OK_BUTTON=1,
            CANCEL_BUTTON=2,
            EXTRA_BUTTON=3;
    void onPress(int button)
    {
        view.dismiss();
        if(singleEmmiter!=null)
            singleEmmiter.onSuccess(button);
        deletePresenter();

    }

    @Override
    public void onCancel() {
        onPress(CANCEL_BUTTON);
    }

    private boolean needToClose=false;
    public void cancelMessageBox()
    {
        needToClose = true;
        if(view!=null)
            onPress(CANCEL_BUTTON);
    }

    SingleEmitter singleEmmiter=null;
    Single<Integer> pressObservable=Single.create(new SingleOnSubscribe() {
        @Override
        public void subscribe(@NonNull SingleEmitter e) throws Exception {
            singleEmmiter = e;
        }
    });
    public Single<Integer> getSingle()
    {
          return pressObservable;
    }

//-----------------------------
    public static MvpMessageBoxPresenter createDialog(FragmentManager fragmentManager,
                                                      int iconResId,
                                                      String title, String msg, boolean askDontShowAgain,
                                                      String okTitle, String cancelTitle, String exTitle)
    {
        MvpMessageBoxPresenter presenter = new MvpMessageBoxPresenter();
        long id = presenter.getDialogPresenterId();
        MvpMessageBox fragment = MvpMessageBox.newInstance(id, iconResId, title, msg, askDontShowAgain, okTitle, cancelTitle, exTitle);

        fragment.show(fragmentManager, String.valueOf(id));

        return presenter;
    }



}
