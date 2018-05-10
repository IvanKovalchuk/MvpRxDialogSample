package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;

import com.kivsw.cloud.DiskContainer;
import com.kivsw.mvprxdialog.Contract;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;


/**
 * this class is a dialog to selects a dir
 */

public class MvpRxSelectDirDialogPresenter extends MvpRxFileDialogPresenter {
    public static MvpRxSelectDirDialogPresenter createDialog(@NonNull Context context, @NonNull FragmentManager fragmentManager,
                                                             @NonNull DiskContainer disks, @Nullable String defaultPath, @Nullable String mask)
    {
        Bitmap ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.ico_folder);
        String title = context.getResources().getText(R.string.choose_directory).toString();
        MvpRxSelectDirDialogPresenter presenter = new MvpRxSelectDirDialogPresenter(context, disks, defaultPath, mask);
        long id= presenter.getDialogPresenterId();

        MvpRxFileDialog fragment = MvpRxFileDialog.newInstance(id, ico, title);

        fragment.show(fragmentManager, String.valueOf(id));

        return presenter;
    }

    @Override
    public void setUI(Contract.IView view) {
        super.setUI(view);
        if(this.view != null)
              this.view.showFileNameEdit(false);
    }

    private MvpRxSelectDirDialogPresenter(@NonNull Context context, @NonNull DiskContainer disks, String path, String mask)
    {
        super(context, disks, path, mask);
    }

    /**
     *
     * @return full name of the selected file (with disk name)
     */
    protected String getSelectedFullFileName()
    {
        String res=currentDisk.getScheme()+"://"+getCurrentDir();
        return res;
    }

    public void onOkClick()
    {
        emmiter.onSuccess(getSelectedFullFileName());
        deletePresenter();
    };
}
