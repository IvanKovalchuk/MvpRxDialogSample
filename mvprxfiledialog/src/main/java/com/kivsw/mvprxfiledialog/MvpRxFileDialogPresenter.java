package com.kivsw.mvprxfiledialog;

import android.content.Context;

import com.kivsw.cloud.DiskContainer;
import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.mvprxdialog.BaseMvpPresenter;
import com.kivsw.mvprxdialog.Contract;
import com.kivsw.mvprxdialog.inputbox.MvpInputBoxBuilder;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxBuilder;
import com.kivsw.mvprxdialog.messagebox.MvpMessageBoxPresenter;
import com.kivsw.mvprxfiledialog.data.FileFilter;
import com.kivsw.mvprxfiledialog.data.FileSystemPath;
import com.kivsw.mvprxfiledialog.data.IFileSystemPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * this is the base class for file dialogs
 */

public abstract class MvpRxFileDialogPresenter extends BaseMvpPresenter {
  /*  public enum TypeDialog {OPEN, SAVE, SELDIR}; // Action type of FileDialog

    private TypeDialog typeDialog;*/
    protected Context context;
    protected MvpRxFileDialog view=null;
    private DiskContainer disks;
    private List<IDiskIO.ResourceInfo> fileList=null, visibleFileList=null;
    private boolean progressVisible=true;
    /*private List<String> pathSegments;
    protected IDiskRepresenter currentDisk=null;*/
    protected IFileSystemPath fileSystemPath;
    protected FileFilter filter=new FileFilter();

    protected  MaybeEmitter<String> emmiter=null;
    protected Maybe<String> maybe = Maybe.create(new MaybeOnSubscribe<String>() {
        @Override
        public void subscribe(@NonNull MaybeEmitter<String> e) throws Exception {
            emmiter = e;
        }
    });

    final static String UP_DIR_NAME="..";

    @Override
    public Contract.IView getUI() {
        return view;
    }

    @Override
    public void setUI(@NonNull Contract.IView view) {
        this.view = (MvpRxFileDialog)view;
        this.view.setDiskContainer(disks);
        setViewData(null);
    }

    @Override
    public void removeUI()
    {
        this.view = null;
    };

    protected MvpRxFileDialogPresenter(Context context, @NonNull DiskContainer diskList, @Nullable String path, @Nullable String mask)
    {
        this.context = context.getApplicationContext();
        this.disks = (diskList);
        fileList = new ArrayList();
        visibleFileList = fileList;

        fileSystemPath = new FileSystemPath(diskList);
        fileSystemPath.setFullPath(path);

        filter.setMask(mask);

        if(fileSystemPath.getCurrentDisk()!=null)
            updateDir(true, null);
        else
            selectDiskList();

        registerDialogPresenter();
    };


    public DiskContainer getDisks() {
        return disks;
    }

    /**
     * set all UI data
     * @param itemToPos
     */
    protected void setViewData(String itemToPos)
    {
        if(view==null)
            return;

        if(diskListVisible)
        {  // shows disk list
            view.showProgress(false);
            view.setDiskList(disks.getDiskList());
        }
        else
        { // shows file list
            view.setFileList(visibleFileList);
            view.setPath(fileSystemPath, filter.getWildCard());
            view.showProgress(progressVisible);

            if (itemToPos != null)
                view.scrollToItem(itemToPos);
        }

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
            updateDir(false, null);
            return true;
        }
        return false;
    }

    public void onDiskChange(IDiskRepresenter newCurrentDisk)
    {
        /*currentDisk = newCurrentDisk;
        pathSegments.clear();*/
        if(fileSystemPath.getCurrentDisk().getScheme().equals(newCurrentDisk.getScheme()))
            return;
        fileSystemPath.setFullPath(newCurrentDisk.getScheme()+":///");
        updateDir(true, null);
    }

    public void onFileClick(IDiskIO.ResourceInfo fi)
    {
        if(fi.isFolder())
        {
            if(fi.name().equals(UP_DIR_NAME))
            {
                if(fileSystemPath.getDepth()==0)
                    selectDiskList();
                else
                {
                    String dir= fileSystemPath.up();
                    updateDir(true, dir);
                }
            }
            else
            {
                fileSystemPath.addDir(fi.name());
                updateDir(true, null);
            }
        }
        else
        {
            view.setEditText(fi.name());
        }
    }

    public void onDeleteFileClick(final IDiskIO.ResourceInfo fi)
    {
        String  title = context.getText(R.string.Confirmation).toString(),
                msg = String.format(Locale.US, context.getText(R.string.doYouWantToDeleteFile).toString(), fi.name());

        MvpMessageBoxBuilder.newInstance()
                .setText(title, msg)
                .setShowDontAskAgain(false)
                .setOkButton(context.getText(android.R.string.yes))
                .setCancelButton(context.getText(android.R.string.no))
                .build(view.getFragmentManager())
                .getSingle()

                .flatMapCompletable(new Function<Integer, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Integer button) throws Exception {
                        if(button.intValue() == MvpMessageBoxPresenter.OK_BUTTON) {
                            if(fi.isFolder())
                                return disks.deleteDir(fileSystemPath.getFullPath()+fi.name());
                            else
                                return disks.deleteFile(fileSystemPath.getFullPath()+fi.name());
                        }
                        else
                            return Completable.complete();
                    }
                })
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showMessage(e.toString());
                        view.showProgress(false);
                    }

                    @Override
                    public void onComplete() {
                        view.showProgress(false);
                        updateDir(false,null);
                    }
                });
    }
    public void onRenameClick(final IDiskIO.ResourceInfo fi)
    {
        final StringBuilder name=new StringBuilder();
        String title;
        if(fi.isFolder()) title = context.getText(R.string.enter_dir_name).toString();
        else  title = context.getText(R.string.enter_file_name).toString();

        MvpInputBoxBuilder.newInstance()
                .setText(title, "")
                .setInitialValue(fi.name())
                .build(view.getFragmentManager())
                .getMaybe()
                .flatMapCompletable(new Function<String, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull String newName) throws Exception {
                        view.showProgress(true);
                        String  oldName=fileSystemPath.getFullPath()+fi.name();//getCurrentDir()+fi.name();
                        newName=fileSystemPath.getFullPath()+newName;//getCurrentDir()+newName;
                        name.append(newName);
                        if(fi.isFolder())  return disks.renameDir(oldName, newName);
                         else   return disks.renameFile(oldName, newName);
                    }
                })
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showMessage(e.toString());
                        view.showProgress(false);
                    }

                    @Override
                    public void onComplete() {
                        view.showProgress(false);
                        updateDir(false, name.toString());
                    }
                });
    }

    public void onCreateDirClick()
    {
        final StringBuilder name=new StringBuilder();

        MvpInputBoxBuilder.newInstance()
                .setText(context.getText(R.string.enter_dir_name), context.getText(R.string.create_dir))
                .build(view.getFragmentManager())
                .getMaybe()
                .flatMapCompletable(new Function<String, Completable>() {
                    @Override
                    public Completable apply(@NonNull String newDir) throws Exception {

                        name.append(newDir);
                        view.showProgress(true);
                        return  disks.createDir(fileSystemPath.getFullPath()+newDir);
                    }
                })
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) { }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showMessage(e.toString());
                        view.showProgress(false);
                    }

                    @Override
                    public void onComplete() {
                        view.showProgress(false);
                        updateDir(false, name.toString());
                    }
                });
    }

    public void onBackPressed()
    {
        if(diskListVisible)
            deletePresenter(); // closes dialog
        else
            onFileClick(createUpdir()); // goes to the updir

    }

    private boolean diskListVisible=false;
    protected void selectDiskList()
    {
        diskListVisible=true;
        setViewData(null);
    };

    private Disposable updateDirDisposable=null;
    protected void updateDir(boolean cleanContext, final String itemToPos)
    {
        diskListVisible=false;

        if(cleanContext) { // clean before update
            visibleFileList.clear();
            visibleFileList.add(createUpdir());
        }

        progressVisible=true;
        setViewData(itemToPos);
        if(updateDirDisposable!=null)
            updateDirDisposable.dispose();
        updateDirDisposable=null;

/*        final IDiskIO disk=currentDisk.getDiskIo();
        disk.authorizeIfNecessary()*/
        disks.authorizeIfNecessary(fileSystemPath.getFullPath())
                .andThen(Single.just("") )
                .flatMap(new Function<String, SingleSource<IDiskIO.ResourceInfo>>() {
                    @Override
                    public SingleSource<IDiskIO.ResourceInfo> apply(@NonNull String s) throws Exception {
                        return disks.getResourceInfo(fileSystemPath.getFullPath());
                    }
                })
                .map(new Function<IDiskIO.ResourceInfo, List<IDiskIO.ResourceInfo>>() {
                    @Override
                    public List<IDiskIO.ResourceInfo> apply(@NonNull IDiskIO.ResourceInfo resourceInfo) throws Exception {
                        ArrayList<IDiskIO.ResourceInfo> list = new ArrayList<IDiskIO.ResourceInfo>();
                        //list.add(createUpdir());
                        if(resourceInfo.content()!=null)
                            list.addAll(
                                    filter.filterList(resourceInfo.content())
                                    );
                        else {
                            list.add(createUpdir());
                            throw new Exception(context.getText(R.string.cant_read_dir).toString());
                        }

                        Collections.sort(list, new Comparator<IDiskIO.ResourceInfo>()
                        {
                            @Override
                            public int compare(IDiskIO.ResourceInfo lhs, IDiskIO.ResourceInfo rhs) {

                                int r=0;
                                if(lhs.isFolder()==rhs.isFolder())
                                    r= lhs.name().compareToIgnoreCase(rhs.name());
                                else
                                {
                                    if(lhs.isFolder()) r=-1;
                                    else r=1;
                                }
                                return r;
                            }
                        });

                        list.add(0,createUpdir());
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

                        setViewData(itemToPos);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        String err=disks.errorToString(e);
                        if(view!=null)
                           view.showMessage(err);
                        progressVisible=false;

                        setViewData(itemToPos);

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
                return UP_DIR_NAME;
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

    /*protected String getCurrentDir()
    {
        StringBuilder res = new StringBuilder();
        for(String segment:pathSegments) {
            res.append('/');
            res.append(segment);
        }
        res.append('/');
        return res.toString();
    };*/

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
        String res=fileSystemPath.getFullPath()+getSelectedFile();//currentDisk.getScheme()+"://"+getCurrentDir() + getSelectedFile();
        return res;
    }

    public void onDiskClick(IDiskRepresenter dsk,  int position)
    {
        /*currentDisk = dsk;
        pathSegments.clear();*/
        fileSystemPath.setFullPath(dsk.getScheme()+":///");
        updateDir(true,null);
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
