package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;

import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.PresenterManager;

import java.util.List;

/**
 * Created by ivan on 9/10/2017.
 */

public class MvpRxOpenFileDialogPresenter extends MvpRxFileDialogPresenter {

    public static MvpRxOpenFileDialogPresenter createDialog(Context context, FragmentManager fragmentManager, List<IDiskRepresenter> disks, String defaultPath)
    {

        Bitmap ico = BitmapFactory.decodeResource(context.getResources(), R.drawable.ico_folder);
        String title = context.getResources().getText(R.string.open_file).toString();
        MvpRxOpenFileDialogPresenter presenter = new MvpRxOpenFileDialogPresenter(disks, defaultPath);
        long id= PresenterManager.getInstance().addNewPresenter(presenter);

        MvpRxFileDialog fragment = MvpRxFileDialog.newInstance(id, ico, title);

        fragment.show(fragmentManager, String.valueOf(id));

        return presenter;
    }

    private MvpRxOpenFileDialogPresenter(List<IDiskRepresenter> disks, String path)
    {
        super(disks, path);
    }

    public void onOkClick()
    {};
}
