package com.kivsw.cloud;

import android.net.Uri;

import com.kivsw.cloud.disk.IDiskIO;
import com.kivsw.cloud.disk.IDiskRepresenter;
import com.kivsw.cloud.disk.localdisk.LocalDiskRepresenter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;


/**
 * This class contains all available disks and may be used as proxy
 * any remote path MUST have sceme
 */

public class DiskContainer {

    private List<IDiskRepresenter> diskList;

    public DiskContainer(List<IDiskRepresenter> disks)
    {
        this(disks.toArray(new IDiskRepresenter[disks.size()]));
    };
    public DiskContainer(IDiskRepresenter... disks)
    {
        diskList = new CopyOnWriteArrayList();//Collections.synchronizedList(new ArrayList(disks.length));

        addDisk(disks);
    };
    public void addDisk(IDiskRepresenter... disks)
    {
        diskList.addAll(Arrays.asList(disks));
    };

    public List<IDiskRepresenter> getDiskList()
    {
        return diskList;
    }

    public Completable authorize(String urlPath)
    {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().authorize();
    };

    public Completable authorizeIfNecessary(String urlPath)
    {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().authorizeIfNecessary();
    };

    public Completable isAuthorized(String urlPath)
    {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().isAuthorized();
    };

    public Single<IDiskIO.DiskInfo> getRequestDiskInfo(String urlPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().getRequestDiskInfo();
    }


    public Single<IDiskIO.ResourceInfo> getResourceInfo(String urlPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Single.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().getResourceInfo(cloudFile.getPath());
    }


    public Completable createDir(String urlPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Single.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().createDir(cloudFile.getPath());
    }


    public Completable renameDir(String urlPath, String newPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));

        CloudFile newCloudFile=parseFileName(newPath);
        if(newCloudFile==null)
            Completable.error(new Exception("incorrect path "+newPath));

        if(!cloudFile.uri.getScheme().equals(newCloudFile.uri.getScheme()))
            return Completable.error(new Exception("It's impossible to change scheme"));

        return cloudFile.diskRepresenter.getDiskIo().renameDir(cloudFile.getPath(), newCloudFile.getPath());
    }


    public Completable renameFile(String urlPath, String newPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));

        CloudFile newCloudFile=parseFileName(newPath);
        if(newCloudFile==null)
            Completable.error(new Exception("incorrect path "+newPath));

        if(!cloudFile.uri.getScheme().equals(newCloudFile.uri.getScheme()))
            return Completable.error(new Exception("It's impossible to change scheme"));

        return cloudFile.diskRepresenter.getDiskIo().renameFile(cloudFile.getPath(), newCloudFile.getPath());
    }


    public Completable deleteDir(String urlPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));
        return cloudFile.diskRepresenter.getDiskIo().deleteDir(cloudFile.getPath());
    }


    public Completable deleteFile(String urlPath) {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+urlPath));

        return cloudFile.diskRepresenter.getDiskIo().deleteFile(cloudFile.getPath());
    }


    public Observable<Integer> downloadFile(String remoteUrlPath, String localPath) {
        CloudFile cloudFile=parseFileName(remoteUrlPath);
        if(cloudFile==null)
            Completable.error(new Exception("incorrect path "+remoteUrlPath));

        CloudFile localFile=parseFileName(localPath);
        if(localFile==null)
            Completable.error(new Exception("incorrect path "+localPath));

        return cloudFile.diskRepresenter.getDiskIo().downloadFile(cloudFile.getPath(), localFile.uri.getPath());
    }


    public Observable<Integer> uploadFile(String remoteUrlPath, String localPath) {
        CloudFile cloudFile=parseFileName(remoteUrlPath);
        if(cloudFile==null)
            Observable.error(new Exception("incorrect path "+remoteUrlPath));

        CloudFile localFile=parseFileName(localPath);
        if(localFile==null)
            Observable.error(new Exception("incorrect path "+localPath));

        return cloudFile.diskRepresenter.getDiskIo().uploadFile(cloudFile.getPath(), localFile.uri.getPath());
    };

    public boolean isLocalStorage(String urlPath) throws Exception
    {
        CloudFile cloudFile=parseFileName(urlPath);
        if(cloudFile==null)
            throw new Exception("incorrect path "+urlPath);
        return cloudFile.diskRepresenter.getDiskIo().isLocalStorage();
    }

    static public class CloudFile
    {
        public IDiskRepresenter diskRepresenter;
        public Uri uri;
        public String getPath(){return uri.getPath();}
    }

    public CloudFile parseFileName(String path)
    {

        Uri uri = Uri.parse(path);
        String scheme = uri.getScheme();

        IDiskRepresenter diskRepresenter=findDisk( scheme);
        if(diskRepresenter==null)
            return null;

        CloudFile cf=new CloudFile();
        cf.uri = uri;
        cf.diskRepresenter = diskRepresenter;
        return cf;

    }
    final static String DEFAULT_SCHEME= LocalDiskRepresenter.ROOTFS_SCHEME;
    protected IDiskRepresenter findDisk(String scheme)
    {
        if(scheme==null)
            scheme = DEFAULT_SCHEME;
        for(IDiskRepresenter dsk:diskList)
            if(dsk.getScheme().equals(scheme))
                return dsk;

        return null; // return default disk
    }



}
