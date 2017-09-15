package com.kivsw.mvprxfiledialog;

import android.content.Context;
import android.net.Uri;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by ivan on 9/7/2017.
 */

public abstract class MvpRxFileDialogPresenter extends BaseMvpPresenter {
  /*  public enum TypeDialog {OPEN, SAVE, SELDIR}; // Action type of FileDialog

    private TypeDialog typeDialog;*/
    protected Context context;
    protected MvpRxFileDialog view=null;
    private List<IDiskRepresenter> disks;
    private List<IDiskIO.ResourceInfo> fileList=null, visibleFileList=null;
    private boolean progressVisible=true;
    private List<String> pathSegments;
    protected IDiskRepresenter currentDisk=null;
    protected FileFilter filter=new FileFilter();

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
        setViewData();
    }

    protected MvpRxFileDialogPresenter(Context context, List<IDiskRepresenter> disks, String path, String mask)
    {
        this.context = context.getApplicationContext();
        this.disks = disks;
        fileList = new ArrayList();
        visibleFileList = fileList;

        if(path != null) {
            Uri uri = Uri.parse(path);
            String scheme = uri.getScheme();
            pathSegments = new ArrayList(uri.getPathSegments());
            currentDisk = getDisk(scheme);
        }
        else
            pathSegments = new ArrayList();

        filter.setMask(mask);

        if(currentDisk!=null)
            updateDir(true);
        else
            selectDiskList();
    };

    protected IDiskRepresenter getDisk(String scheme)
    {
        for(IDiskRepresenter dsk:disks)
            if(dsk.getScheme().equals(scheme))
            {
                return dsk;
            }
        return null;
    }

    protected void setViewData()
    {
        if(view==null)
            return;

        view.setFileList(visibleFileList);
        view.setPath(getCurrentDir()+filter.getWildCard());
        view.showProgress(progressVisible);
    }

    /**
     * set a filter for file List
     * @param mask
     * @return  false if mask is not a valid filter
     *          TRUE otherwise
     */
    protected boolean setFileListFilter(String mask)
    {
        if(filter.isMask(mask))
        {
            filter.setMask(mask);
            if(view!=null)
                view.setEditText("");
            updateDir(false);
            return true;
        }
        return false;
    }

    public void onFileClick(IDiskIO.ResourceInfo fi, int position)
    {
        if(fi.isFolder())
        {
            if(fi.name().equals(".."))
            {
                if(pathSegments.size()==0)
                    selectDiskList();
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

    protected void selectDiskList()
    {
        view.showProgress(false);
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
        setViewData();
        if(updateDirDisposable!=null)
            updateDirDisposable.dispose();
        updateDirDisposable=null;

        final IDiskIO disk=currentDisk.getDiskIo();

        disk.authorizeIfNecessary()
                .andThen( disk.getResourceInfo(getCurrentDir() ) )
                .map(new Function<IDiskIO.ResourceInfo, List<IDiskIO.ResourceInfo>>() {
                    @Override
                    public List<IDiskIO.ResourceInfo> apply(@NonNull IDiskIO.ResourceInfo resourceInfo) throws Exception {
                        ArrayList<IDiskIO.ResourceInfo> list = new ArrayList<IDiskIO.ResourceInfo>();
                        list.add(createUpdir());
                        if(resourceInfo.content()!=null)
                            list.addAll( filter.filterList(resourceInfo.content()) );
                        else
                            throw new Exception(context.getText(R.string.cant_read_dir).toString());
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

                        setViewData();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        String err=disk.getErrorString(e);
                        if(view!=null)
                           view.showMessage(err);
                        progressVisible=false;

                        setViewData();

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

    /**
     *
     * @return only file name that EditText has
     */
    protected String getSelectedFile()
    {
        if(view==null)
            return null;

        String fileName = view.getEditText();
        if(fileName==null || fileName.length()==0)
            return null;
        return fileName;
    }

    /**
     *
     * @return full name of the selected file (with disk name)
     */
    protected String getSelectedFullFileName()
    {
        String res=currentDisk.getScheme()+"://"+getCurrentDir() + getSelectedFile();
        return res;
    }

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
