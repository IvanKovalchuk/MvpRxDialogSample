package com.kivsw.cloud.disk.localdisk;


import com.kivsw.cloud.disk.IDiskIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ivan on 8/22/17.
 */

public class LocalDiskIo implements IDiskIO {
    String mountPoint; // path to the mount point

    /**
     * returns all the storage devices
     * @return
     */
    static public HashSet<String> getExternalMounts()//https://stackoverflow.com/questions/11281010/how-can-i-get-external-sd-card-path-for-android-4-0
    {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        StringBuilder s = new StringBuilder();
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s.append(new String(buffer));
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.toString().split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }
    public LocalDiskIo()
    {
        this("/");
    }

    public LocalDiskIo(String mountPoint)
    {
        if(mountPoint.charAt(mountPoint.length()-1)==File.separatorChar)
            this.mountPoint = mountPoint;
        else
            this.mountPoint = mountPoint+File.separatorChar;
    }

    @Override
    public Completable authorize() {
        return Completable.complete();
    }

    @Override
    public Completable authorizeIfNecessary() {
        return Completable.complete();
    }

    @Override
    public Completable isAuthorized() {
        return Completable.complete();
    }

    private DiskInfo getDiskInfo()
    {
        DiskInfo di= new DiskInfo(){

            long size, free, used;
            {
                File f = new File(mountPoint);
                size = f.getTotalSpace();
                free = f.getFreeSpace();
                used = size - free;
            }

            @Override
            public long size() {
                return size;
            }

            @Override
            public long freeSize() {
                return free;
            }

            @Override
            public long used() {
                return used;
            };
        };

        return di;
    }

    @Override
    public Single<DiskInfo> getRequestDiskInfo() {

        return Single.fromCallable(new Callable<DiskInfo>() {
            @Override
            public DiskInfo call() throws Exception {
                return getDiskInfo();
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * adds mountPoint to the path if it's necessary
     * @param path
     * @return
     */
    private String correctPath(String path)
    {
        if((path.length()==0)||(path.charAt(0)!=File.separatorChar))
            path = mountPoint + path;
        else
            path = mountPoint + path.substring(1);
        return path;
    }

    /**
     * file/folder information
     */
    class LocalResourceInfo implements ResourceInfo
    {
        private long size, modified;
        private boolean isFolder, isFile;
        private String name;
        private ArrayList<ResourceInfo> content;


            LocalResourceInfo(String path) throws Exception
            {
                 this(path, true, false);
            }
            protected LocalResourceInfo(String devicePath, boolean contentList, boolean ignoreExistance) throws Exception
            {
                String path = correctPath(devicePath);

                File f = new File(path);
                if(!f.exists() && !ignoreExistance)
                    throw new FileNotFoundException("File does not exist "+path);

                size = f.length();
                isFolder = f.isDirectory();
                isFile = f.isFile();
                name = f.getName();
                modified = f.lastModified();

                if(contentList && isFolder) {

                    if(path.charAt(path.length()-1)!=File.separatorChar)
                        path = path + File.separatorChar;

                    String[] list = f.list();
                    if(list!=null) {
                        content = new ArrayList(list.length);
                        for (String item : list) {
                            if (item.equals(".") || item.equals(".."))
                                continue;
                            try {
                                content.add(new LocalResourceInfo(devicePath + item, false,true));
                            }catch(Exception e)
                            {
                                content.add(new LocalResourceInfo(devicePath + item, false, true));
                            }
                        };
                    }
                }

            }

            @Override
            public long size() {
                return size;
            }

            @Override
            public boolean isFolder() {
                return isFolder;
            }
        @Override
        public boolean isFile() {
            return isFile;
        }
            @Override
            public String name() {
                return name;
            }

            @Override
            public List<ResourceInfo> content() {
                return content;
            }

            @Override
            public long modified() {
                return modified;
            }

            @Override
            public long created() {
                return modified;
            }


    };

    @Override
    public Single<ResourceInfo> getResourceInfo(final String path) {

        return Single.fromCallable(new Callable<ResourceInfo>() {
                    @Override
                    public ResourceInfo call() throws Exception {
                        return new LocalResourceInfo(path);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable createDir(final String apath) {

        return Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        String path = correctPath(apath);
                        File f=new File(path);
                        if(!f.mkdir())
                            throw new Exception("Cannot create directory "+path);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable renameDir(final String aPath, final String aNewPath) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                String path = correctPath(aPath),
                       newPath = correctPath(aNewPath);
                File f = new File(path),
                     nf= new File(newPath);
                if(!f.renameTo(nf))
                    throw new Exception("Cannot rename "+path);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable renameFile(String path, String newPath) {
        return renameDir(path, newPath);
    }

    @Override
    public Completable deleteDir(final String aPath) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                String path = correctPath(aPath);
                File f = new File(path);
                if(!f.delete())
                    throw new Exception("Cannot delete "+path);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deleteFile(String path) {
        return deleteDir(path);
    }

    @Override
    public Observable<Integer> downloadFile(String remotePath, String localPath) {
        return Observable.error(new Exception("not implemented"));
    }

    @Override
    public Observable<Integer> uploadFile(String remotePath, String localPath) {
        return Observable.error(new Exception("not implemented"));
    }

    @Override
    public String getErrorString(Throwable throwable) {
        return throwable.getMessage();
    }

    @Override
    public String convertToLocalPath(String devicePath)
    {
        return correctPath(devicePath);
    };
}
