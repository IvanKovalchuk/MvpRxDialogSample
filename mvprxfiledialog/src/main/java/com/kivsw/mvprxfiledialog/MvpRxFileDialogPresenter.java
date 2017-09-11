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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by ivan on 9/7/2017.
 */

public abstract class MvpRxFileDialogPresenter extends BaseMvpPresenter {
  /*  public enum TypeDialog {OPEN, SAVE, SELDIR}; // Action type of FileDialog

    private TypeDialog typeDialog;*/
    private MvpRxFileDialog view=null;
    private List<IDiskRepresenter> disks;
    private List<IDiskIO.ResourceInfo> fileList=null, visibleFileList=null;
    private boolean progressVisible=true;
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
        setData();
    }

    protected MvpRxFileDialogPresenter(List<IDiskRepresenter> disks, String path)
    {
        this.disks = disks;
        fileList = new ArrayList();
        visibleFileList = fileList;
        Uri uri = Uri.parse(path);
        String scheme=uri.getScheme();
        pathSegments=new ArrayList(uri.getPathSegments());

        for(IDiskRepresenter dsk:disks)
            if(dsk.getScheme().equals(scheme))
            {
                currentDisk=dsk;
                break;
            }

        updateDir(true);
    };

    protected void setData()
    {
        if(view==null)
            return;

        view.setFileList(visibleFileList);
        view.setPath(getCurrentDir());
        view.showProgress(progressVisible);
    }

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

    private Disposable updateDirDisposable=null;
    protected void updateDir(boolean cleanContext)
    {

        if(cleanContext) {
            visibleFileList.clear();
            visibleFileList.add(createUpdir());
        }
        progressVisible=true;
        setData();
        if(updateDirDisposable!=null)
            updateDirDisposable.dispose();
        updateDirDisposable=null;

        final IDiskIO disk=currentDisk.getDiskIo();

        disk.authorizeIfNecessary()
                .andThen(Single.just(""))
                .flatMap(new Function<String, SingleSource<IDiskIO.ResourceInfo>>() {
                    @Override
                    public SingleSource<IDiskIO.ResourceInfo> apply(@NonNull String s) throws Exception {
                        return disk.getResourceInfo(getCurrentDir());
                    }
                })
                .map(new Function<IDiskIO.ResourceInfo, List<IDiskIO.ResourceInfo>>() {
                    @Override
                    public List<IDiskIO.ResourceInfo> apply(@NonNull IDiskIO.ResourceInfo resourceInfo) throws Exception {
                        ArrayList<IDiskIO.ResourceInfo> list = new ArrayList<IDiskIO.ResourceInfo>();
                        list.add(createUpdir());
                        list.addAll(resourceInfo.content());
                        return list;
                    }
                })
                .subscribe(new SingleObserver<List<IDiskIO.ResourceInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        updateDirDisposable = d;
                    }

                    @Override
                    public void onSuccess(@NonNull List<IDiskIO.ResourceInfo> fileList) {
                        MvpRxFileDialogPresenter.this.fileList= fileList;
                        visibleFileList=fileList;
                        progressVisible=false;

                        setData();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        String err=disk.getErrorString(e);
                        if(view!=null)
                           view.showMessage(err);
                        progressVisible=false;

                        setData();

                        updateDirDisposable=null;
                    }
                });
    };
    /** creates FileInfor of ".."-directory
     *
     * @return
     */
    protected IDiskIO.ResourceInfo createUpdir()
    {
        IDiskIO.ResourceInfo ri=new IDiskIO.ResourceInfo(){
            @Override
            public long size() {
                return 0;
            }

            @Override
            public boolean isFolder() {
                return true;
            }

            @Override
            public boolean isFile() {
                return false;
            }

            @Override
            public String name() {
                return "..";
            }

            @Override
            public List<IDiskIO.ResourceInfo> content() {
                return null;
            }

            @Override
            public long modified() {
                return 0;
            }

            @Override
            public long created() {
                return 0;
            }
        };
        return ri;
    }

    protected String getCurrentDir()
    {
        StringBuilder res = new StringBuilder();
        for(String segment:pathSegments) {
            res.append('/');
            res.append(segment);
        }
        res.append('/');
        return res.toString();
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
