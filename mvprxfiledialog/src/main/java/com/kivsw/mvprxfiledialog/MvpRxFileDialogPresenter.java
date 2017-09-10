package com.kivsw.mvprxfiledialog;

import android.net.Uri;
import android.support.v4.app.FragmentManager;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;
import com.kivsw.mvprxdialog.PresenterManager;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBox;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxPresenter;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by ivan on 9/7/2017.
 */

public abstract class MvpRxFileDialogPresenter extends BaseMvpPresenter {
  /*  public enum TypeDialog {OPEN, SAVE, SELDIR}; // Action type of FileDialog

    private TypeDialog typeDialog;*/
    private MvpRxFileDialog view=null;
    private List<IDiskRepresenter> disks;
    private List<String> pathSegments;
    private IDiskRepresenter currentDisk=null;


    protected  MaybeEmitter<String> emmiter=null;
    protected Maybe<String> maybe = Maybe.create(new MaybeOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull MaybeEmitter<String> e) throws Exception {
            emmiter = e;
        }
    });

    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(Contract.IView view) {
        this.view = (MvpRxFileDialog)view;
    }

    protected MvpRxFileDialogPresenter(List<IDiskRepresenter> disks, String path)
    {
        this.disks = disks;

        Uri uri = Uri.parse(path);
        String scheme=uri.getScheme();
        pathSegments=uri.getPathSegments();

        for(IDiskRepresenter dsk:disks)
            if(dsk.getScheme().equals(scheme))
            {
                currentDisk=dsk;
                break;
            }
    };


    public void onFileClick(IDiskIO.ResourceInfo fi, int position)
    {
        if(fi.isFolder())
        {
            if(fi.name().equals(".."))
            {
                if(pathSegments.size()==0)
                    selectDisk();
                else
                {
                    pathSegments.remove(pathSegments.size()-1);
                    updateDir(true);
                }
            }
            else
            {
                pathSegments.add(fi.name());
                updateDir(true);
            }
        }
        else
        {
            view.setEditText(fi.name());
        }
    }

    protected void selectDisk()
    {
        view.setDiskList(disks);
    };

    protected void updateDir(boolean cleanContext)
    {
        if(cleanContext)
            view.setFileList(null);
    };

    public void onDiskClick(IDiskRepresenter dsk,  int position)
    {
        currentDisk = dsk;
        pathSegments.clear();
        updateDir(true);
    }

    public abstract void onOkClick();

    public void onCancelClick()
    {
        deletePresenter();
    }

    protected void deletePresenter()
    {
        view.dismiss();
        super.deletePresenter();
        if(emmiter!=null)
        emmiter.onComplete();
    }

    public Maybe<String> getMaybe()
    {
        return maybe;
    }

}
