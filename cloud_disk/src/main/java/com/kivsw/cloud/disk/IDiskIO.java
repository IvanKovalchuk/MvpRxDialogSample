package com.kivsw.cloud.disk;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.Nullable;

/**
 * Created by ivan on 6/23/17.
 */

// https://habrahabr.ru/post/281360/

public interface IDiskIO {
    Completable authorize();
    Completable authorizeIfNecessary();
    Completable isAuthorized();
    Single<DiskInfo> getRequestDiskInfo();
    /**
     * retrieves file/dir info.
     * observable emits 1 item for a file and may emit several items for a directory. Each item
     * holds a piece of directory content
     * @param path
     * @return observable thatr emits file or directory info
     */
    Observable<ResourceInfo> getResourceInfo(String path);
    Completable createDir(String path);
    Completable renameDir(String path, String newPath);
    Completable renameFile(String path, String newPath);
    Completable deleteDir(String path);
    Completable deleteFile(String path);

    Observable<Integer> downloadFile(String remotePath, String localPath);
    Observable<Integer> uploadFile(String remotePath, String localPath);

    String getErrorString(Throwable throwable); // convert an exception into readable form

    boolean isLocalStorage();
    @Nullable
    String convertToLocalPath(String path); // returns path in local file system or null


    interface DiskInfo{
        long size();
        long freeSize();
        long used();
    };

      interface ResourceInfo{
          long size();
          boolean isFolder();
          boolean isFile();
          String name();
          List<ResourceInfo> content(); // return
          long modified();
          long created();

    };
}
