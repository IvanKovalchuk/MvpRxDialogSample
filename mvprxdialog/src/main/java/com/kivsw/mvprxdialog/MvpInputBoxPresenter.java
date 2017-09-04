package com.kivsw.mvprxdialog;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.text.Editable;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.internal.operators.maybe.MaybeNever;

/**
 * Created by ivan on 9/3/2017.
 */

public class MvpInputBoxPresenter extends BaseMvpPresenter {

    private MvpInputBoxPresenter()
    {}

    MvpInputBox view=null;
    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(Contract.IView view) {
        this.view = (MvpInputBox)view;
    }

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

    MaybeEmitter<String> emmiter=null;
    Maybe<String> maybe = Maybe.create(new MaybeOnSubscribe<String>() {
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
                                                      Bitmap icon,
                                                      String title, String msg, String InputValue,
                                                      int inputType,
                                                      TestValue testValue)
    {
        MvpInputBoxPresenter presenter = new MvpInputBoxPresenter();
        presenter.testValue = testValue;

        long id=PresenterManager.getInstance().addNewPresenter(presenter);
        MvpInputBox fragment = MvpInputBox.newInstance(id, icon, title, msg, InputValue, inputType);

        fragment.show(fragmentManager, String.valueOf(id));
        return presenter;
    }

    public Maybe<String> getMaybe()
    {
        return maybe;
    }
}

