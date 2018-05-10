package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;

import com.kivsw.cloud.DiskContainer;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * Created by ivan on 9/10/2017.
 */

public class MvpRxOpenFileDialogPresenter extends MvpRxFileDialogPresenter {

    public static MvpRxOpenFileDialogPresenter createDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                                            @NonNull DiskContainer disks, @Nullable String defaultPath, @Nullable String mask)
    {

        Bitmap ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.icodir);
        String title = context.getResources().getText(R.string.open_file).toString();
        MvpRxOpenFileDialogPresenter presenter = new MvpRxOpenFileDialogPresenter(context, disks, defaultPath, mask);
        long id= presenter.getDialogPresenterId();

        MvpRxFileDialog fragment = MvpRxFileDialog.newInstance(id, ico, title);

        fragment.show(fragmentManager, String.valueOf(id));

        return presenter;
    }

    private MvpRxOpenFileDialogPresenter(Context context, DiskContainer disks, String path, String mask)
    {
        super(context, disks, path, mask);
    }

    public void onOkClick()
    {
        String fileName = getSelectedFile();
        if(fileName==null)
            return;

        if(setFileListFilter(fileName))
            return;

        emmiter.onSuccess(getSelectedFullFileName());
        deletePresenter();

    };
}
