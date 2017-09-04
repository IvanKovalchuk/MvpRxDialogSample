package com.kivsw.mvprxdialogsample;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.InputType;

import com.kivsw.mvprxdialog.Contract;
import com.kivsw.mvprxdialog.MvpInputBox;
import com.kivsw.mvprxdialog.MvpInputBoxPresenter;
import com.kivsw.mvprxdialog.MvpMessageBoxPresenter;

import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ivan on 9/3/2017.
 */

public class MainActivityPresenter implements Contract.IPresenter {


    static private MainActivityPresenter singletone=null;
    public static MainActivityPresenter getInstance()
    {
        if(singletone==null)
            singletone = new MainActivityPresenter();
        return singletone;
    }

    MainActivity view;

    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(Contract.IView view) {
        this.view = (MainActivity)view;
    }

    long presenter_id;
    @Override
    public void setPresenterId(long id) {
        presenter_id=id;
    }

    @Override
    public long getPresenterId() {
        return presenter_id;
    }


    public void showDialog()
    {
        Bitmap icon = BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher_round);
         MvpMessageBoxPresenter.createDialog(view.getSupportFragmentManager(),  icon, "A Title", "A message",true, "button1", "btn2", "btn3")
                 .getSingle()
                 .subscribe(new SingleObserver<Integer>() {
                     @Override
                     public void onSubscribe(@NonNull Disposable d) {

                     }

                     @Override
                     public void onSuccess(@NonNull Integer btnNum) {
                        view.showMessage(String.valueOf(btnNum) + "th button");
                     }

                     @Override
                     public void onError(@NonNull Throwable e) {
                         view.showMessage( e.toString());
                     }
                 });
    }

    public void showInputBox()
    {
        Bitmap icon = BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher_round);
        MvpInputBoxPresenter.createDialog(view.getSupportFragmentManager(),
                icon, "A Title", "A message", "initial value", InputType.TYPE_CLASS_TEXT,
                 new MvpInputBoxPresenter.TestValue(){
                     @Override
                     public boolean test(MvpInputBoxPresenter presenter, Editable value, StringBuilder errorMessage) {
                         if(value.length()==0) {
                             errorMessage.append("Empty value is not correct");
                             return false;
                         }
                         return true;
                     }
                 })
                .getMaybe()
                .subscribe(new MaybeObserver<String>(){
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull String s) {
                        view.showMessage(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
