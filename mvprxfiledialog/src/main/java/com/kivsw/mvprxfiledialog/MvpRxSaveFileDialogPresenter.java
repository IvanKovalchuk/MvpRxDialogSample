package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxBuilder;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxPresenter;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


/**
 * Created by ivan on 9/10/2017.
 */

public class MvpRxSaveFileDialogPresenter extends MvpRxFileDialogPresenter {
    public static MvpRxSaveFileDialogPresenter createDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                                            @NonNull List<IDiskRepresenter> disks, @Nullable String defaultPath, @Nullable String mask, @Nullable String defaultExt)
    {

        Bitmap ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.ico_save);
        String title = context.getResources().getText(R.string.save_file).toString();
        MvpRxSaveFileDialogPresenter presenter = new MvpRxSaveFileDialogPresenter(context, disks,defaultPath, mask, defaultExt);
        long id= presenter.getDialogPresenterId();

        MvpRxFileDialog fragment = MvpRxFileDialog.newInstance(id, ico, title);

        fragment.show(fragmentManager, String.valueOf(id));

        return presenter;
    }

    private String defaultExt=null;


    private MvpRxSaveFileDialogPresenter(Context context, List<IDiskRepresenter> disks, String path, String mask, String defaultExt)
    {
        super(context, disks, path, mask);

        if(defaultExt!=null && defaultExt.length()>0) {
            if(defaultExt.charAt(0)=='.')  this.defaultExt = defaultExt;
            else                           this.defaultExt = '.'+defaultExt;
        }
    }
    @Override
    protected String getSelectedFile()
    {
        String r=super.getSelectedFile();
        if((defaultExt==null) || (r==null) )
            return r;

        int dot=r.lastIndexOf('.');
        if((dot!=-1) && (dot>r.lastIndexOf('/') )) // if we have an extension
            return r;
        return r+defaultExt;
    }

    public void onOkClick()
    {
        if(setFileListFilter(getSelectedFile()))
            return;

        // at first check whether the selected file exists
        final String fileName= getSelectedFile();
        if(fileName==null) return;

        final String filePath= getCurrentDir();


        view.showProgress(true);

        currentDisk.getDiskIo().getResourceInfo(filePath) // read dir content
                .flatMap(new Function<IDiskIO.ResourceInfo, SingleSource<Integer>>() {
                    @Override
                    public SingleSource<Integer> apply(@NonNull IDiskIO.ResourceInfo resourceInfo) throws Exception {
                        view.showProgress(false);

                        boolean fileExists=false; // looks for the selected file
                        for(IDiskIO.ResourceInfo file:resourceInfo.content())
                        {
                            if(file.name().equals(fileName))
                            {
                                fileExists=true;
                                break;
                            }
                        }

                        if(fileExists) {
                            MvpMessageBoxPresenter dlg = MvpMessageBoxBuilder.newInstance()
                                    .setText(context.getText(R.string.Confirmation).toString(), context.getText(R.string.file_exists).toString())
                                    .setOkButton(context.getText(android.R.string.yes).toString())
                                    .setCancelButton(context.getText(android.R.string.no).toString())
                                    .build(view.getFragmentManager());

                            return dlg.getSingle();
                        }
                        else   return Single.just(Integer.valueOf(MvpMessageBoxPresenter.OK_BUTTON));
                    }
                })

                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer btnNum) {

                        if(btnNum.intValue()==MvpMessageBoxPresenter.OK_BUTTON) {
                            emmiter.onSuccess(getSelectedFullFileName());
                            deletePresenter();
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        String err=e.toString();
                        if(view!=null)
                            view.showMessage(err);
                    }
                });

    };
}
