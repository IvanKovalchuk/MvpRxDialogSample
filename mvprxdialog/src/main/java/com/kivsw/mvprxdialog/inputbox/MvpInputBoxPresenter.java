package com.kivsw.mvprxdialog.inputbox;

import android.support.v4.app.FragmentManager;
import android.text.Editable;

import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by ivan on 9/3/2017.
 */

public class MvpInputBoxPresenter extends BaseMvpPresenter {

    private MvpInputBoxPresenter()
    {
        registerDialogPresenter();
    }

    private MvpInputBox view=null;
    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(@NonNull Contract.IView view) {
        this.view = (MvpInputBox)view;
    }
    @Override
    public void removeUI()
    {
        this.view = null;
    };
    /**
     * Inteface for checking a value
     */
    public interface TestValue
    {
        /**
         * @param presenter that has input the value
         * @param value     the value
         * @param errorString  you can write error message here
         * @return true if value is correct and false otherwise
         */
         boolean test(MvpInputBoxPresenter presenter, Editable value, StringBuilder errorString);
    }

    private MaybeEmitter<String> emmiter=null;
    private Maybe<String> maybe = Maybe.create(new MaybeOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull MaybeEmitter<String> e) throws Exception {
            emmiter = e;
        }
    });

    private TestValue testValue=null;

    void onOkPress(Editable inputValue)
    {
        StringBuilder errorMessage=new StringBuilder();
        if(testValue!=null && !testValue.test(this, inputValue, errorMessage)) {
            view.showErrorMessage(errorMessage);
            return;
        }

        if(emmiter!=null)
            emmiter.onSuccess(inputValue.toString());
        deletePresenter();
    }

    void onCancelPress()
    {
        onCancel();
    };
    @Override
    protected void deletePresenter()
    {
        view.dismiss();
        super.deletePresenter();
        if(emmiter!=null)
            emmiter.onComplete();
    }

    //----------------------------------------
    public static MvpInputBoxPresenter createDialog(FragmentManager fragmentManager,
                                                        int iconResId,
                                                      String title, String msg, String InputValue,
                                                      int inputType,
                                                      TestValue testValue)
    {
        MvpInputBoxPresenter presenter = new MvpInputBoxPresenter();
        presenter.testValue = testValue;
        long id=presenter.getDialogPresenterId();

        MvpInputBox fragment = MvpInputBox.newInstance(id, iconResId, title, msg, InputValue, inputType);

        fragment.show(fragmentManager, String.valueOf(id));
        return presenter;
    }

    public Maybe<String> getMaybe()
    {
        return maybe;
    }
}

