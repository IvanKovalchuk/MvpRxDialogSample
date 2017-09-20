package com.kivsw.mvprxdialogsample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;

import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.cloud.disk.localdisk.LocalDiskRepresenter;
import com.kivsw.cloud.disk.localdisk.StorageUtils;
import com.kivsw.cloud.disk.pcloud.PcloudRepresenter;
import com.kivsw.cloud.disk.yandex.YandexRepresenter;
import com.kivsw.mvprxdialog.Contract;
import com.kivsw.mvprxdialog.inputbox.MvpInputBoxBuilder;
import com.kivsw.mvprxdialog.inputbox.MvpInputBoxPresenter;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxBuilder;
import com.kivsw.mvprxfiledialog.MvpRxOpenFileDialogPresenter;
import com.kivsw.mvprxfiledialog.MvpRxSaveFileDialogPresenter;
import com.kivsw.mvprxfiledialog.MvpRxSelectDirDialogPresenter;

import java.util.ArrayList;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

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
        MvpMessageBoxBuilder.newInstance()
                .setIcon(icon)
                .setText("A Title", "A message")
                .showCancelButton()
                .showExtraButton()
                .build(view.getSupportFragmentManager())
                .getSingle()
                .flatMap(new Function<Integer, Single<Integer>>(){
                    @Override
                    public Single<Integer> apply(@NonNull Integer integer) throws Exception {
                        return
                        MvpMessageBoxBuilder.newInstance()
                                .setText("Another Title", "Another message")
                                .build(view.getSupportFragmentManager())
                                .getSingle();
                    }
                })
        // MvpMessageBoxPresenter.createDialog(view.getSupportFragmentManager(),  icon, "A Title", "A message",true, "button1", "btn2", "btn3")                 .getSingle()
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

    public MainActivityPresenter() {
        //this.view = view;
    }

    public void showInputBox()
    {
        Bitmap icon = BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_launcher_round);
        /*MvpInputBoxPresenter.createDialog(view.getSupportFragmentManager(),
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
                 })*/
                MvpInputBoxBuilder.newInstance()
                        .setIcon(icon)
                        .setText("A Title", "A message")
                        .setInputType(InputType.TYPE_CLASS_NUMBER)

                        .setTest( new MvpInputBoxPresenter.TestValue() {
                            @Override
                            public boolean test(MvpInputBoxPresenter presenter, Editable value, StringBuilder errorMessage) {
                                if (value.length() == 0) {
                                    errorMessage.append("Empty value is not correct");
                                    return false;
                                }
                                return true;
                            }
                        })
                        .build(view.getSupportFragmentManager())

                .getMaybe()
                .flatMap(new Function<String, Maybe<String>>(){
                    @Override
                    public Maybe<String> apply(@NonNull String s) throws Exception {
                        return
                        MvpInputBoxBuilder.newInstance()
                                .setText("Another Title", "Another message")
                                .setInitialValue("initial value: "+s)
                                .build(view.getSupportFragmentManager())
                                .getMaybe();
                    }
                })
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

    public void  showFileOpen()
    {

        MvpRxOpenFileDialogPresenter.createDialog(view, view.getSupportFragmentManager(), getDisks(), "file:///", null)
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
    public void  showFileSave()
    {

        MvpRxSaveFileDialogPresenter.createDialog(view, view.getSupportFragmentManager(), getDisks(), "file:///", "", "xxx")
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
    public void  showChooseDir()
    {

        String dir = view.getCacheDir().getAbsolutePath();
        String exDir=  view.getExternalCacheDir().getAbsolutePath();
        String external = Environment.getExternalStorageDirectory().getAbsolutePath();

        MvpRxSelectDirDialogPresenter.createDialog(view, view.getSupportFragmentManager(), getDisks(), "file://"+external, null)
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

    ArrayList<IDiskRepresenter> getDisks()
    {
        ArrayList<IDiskRepresenter> disks=new ArrayList();
        disks.add(LocalDiskRepresenter.getLocalFS(view.getApplicationContext()));
        /*disks.add(LocalDiskRepresenter.getExternalSD(view.getApplicationContext()));*/
        disks.addAll(StorageUtils.getSD_list(view.getApplicationContext()));
        disks.add(new PcloudRepresenter(view.getApplicationContext(), "LPGinE9RXlS"));
        disks.add(new YandexRepresenter(view.getApplicationContext(), "e0b45e7f385644e9af23b7a3b3862ac4", null, null));

        return disks;
    }

}
