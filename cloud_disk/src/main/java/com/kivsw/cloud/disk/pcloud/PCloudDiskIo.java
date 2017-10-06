package com.kivsw.cloud.disk.pcloud;

import android.content.Context;

import com.kivsw.cloud.OAuth.BaseIOAuthCore;
import com.kivsw.cloud.OAuth.PCloudOAuthCore;
import com.kivsw.cloud.disk.BaseDiskIO;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Created by ivan on 8/11/17.
 */



public class PCloudDiskIo extends BaseDiskIO {

    String clientId, auth;

    API.Requests requests;
    API.ConvertorlessRequests convertorlessRequests;
    //API.RequestListRevisions requestListRevisions;

    public PCloudDiskIo(Context cnt, String clientId) {

        super(cnt);
        this.clientId = clientId;

        initRetrofit();
    }

    protected void initRetrofit()
    {
        requests = API.createRequests();
        convertorlessRequests = API.createConvertorlessRequests();
    }

    protected void checkError(API.ErrorInfo errInfo) throws Exception
    {
        if(errInfo.result==0) return;

        throw new Exception(errInfo.result + " " + errInfo.error);
    };
    @Override
    public BaseIOAuthCore createIOAuthCore()
    {
        return PCloudOAuthCore.newInstance(clientId, null);
    };

    @Override
    public Completable isAuthorized()
    {
        if(tokenKeeper.getToken() == null)
            return Completable.error(new NoAuthorizedException());

        return requests.requestUserInfo(tokenKeeper.getToken())
                .subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<API.UserInfo>(){
                    @Override
                    public void accept(@NonNull API.UserInfo usrinf) throws Exception {
                        if(usrinf.result !=0)
                            throw new NoAuthorizedException(usrinf.result + " " + usrinf.error);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();
    };

    @Override
    public Single<DiskInfo> getRequestDiskInfo() {

        return requests.requestUserInfo(tokenKeeper.getToken())
                .subscribeOn(Schedulers.io())
                .map(new Function<API.UserInfo, DiskInfo >(){
                    @Override
                    public DiskInfo apply(API.UserInfo disk) throws Exception{
                        checkError(disk);
                        return disk;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    protected Single<API.MetadataContainer> getFolderInfo(String path)
    {
        if((path.length()>1) && (path.charAt(path.length()-1)=='/'))
            path = path.substring(0, path.length()-1);

        return
        requests.requestListFolder(tokenKeeper.getToken(), path)//requestListRevisions(tokenKeeper.getToken(), path)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());

    }
    protected Single<API.MetadataContainer> getRenameFile(String path, String newPath) {
        return
                requests.requestRenameFile(tokenKeeper.getToken(), path, newPath)
                        //.observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .map(new Function<API.MetadataContainer, API.MetadataContainer>() {
                            @Override
                            public API.MetadataContainer apply(API.MetadataContainer disk) throws Exception{// TODO transforming data format
                                return disk;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Single<ResourceInfo> getResourceInfo(String path) {
        Single<API.MetadataContainer> folder, file;

        folder =  getFolderInfo(path);//requestListRevisions(tokenKeeper.getToken(), path)
        file = getRenameFile(path, path);

        Single res=
        Single.zip(folder, file, new BiFunction<API.MetadataContainer, API.MetadataContainer, ResourceInfo>(){

            @Override
            public ResourceInfo apply(@NonNull API.MetadataContainer folderMeta, @NonNull API.MetadataContainer fileMeta) throws Exception {
                API.MetadataContainer result;
                if(folderMeta.result==0)
                    result = folderMeta;
                else
                    result = fileMeta;

                checkError(result);
                return result.metadata;
            }
        })
      // .subscribeOn(Schedulers.io())
       .observeOn(AndroidSchedulers.mainThread());

        return res;
    }

    @Override
    public Completable createDir(String path) {
        return requests.requestCreateFolder(tokenKeeper.getToken(), path)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.MetadataContainer, Object>() {
                    @Override
                    public Object apply(@NonNull API.MetadataContainer metadataContainer) throws Exception {
                        checkError(metadataContainer);
                        return metadataContainer.metadata;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();
    }

    @Override
    public Completable renameDir(String path, String newPath) {
        if(newPath.charAt(newPath.length()-1)=='/') // removes the last "/" because it will change the behavour according https://docs.pcloud.com/methods/folder/renamefolder.html
            newPath = newPath.substring(0, newPath.length()-1);

        return requests.requestRenameFolder(tokenKeeper.getToken(), path, newPath)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.MetadataContainer, Object>() {
                    @Override
                    public Object apply(@NonNull API.MetadataContainer metadataContainer) throws Exception {
                        checkError(metadataContainer);
                        return metadataContainer.metadata;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();
    }

    @Override
    public Completable renameFile(String path, String newPath) {
        return
                getRenameFile(path, newPath)
                        .subscribeOn(Schedulers.io())
                        .map(new Function<API.MetadataContainer, Object>() {
                            @Override
                            public Object apply(@NonNull API.MetadataContainer metadataContainer) throws Exception {
                                checkError(metadataContainer);
                                return metadataContainer.metadata;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .toCompletable();
    }

    @Override
    public Completable deleteDir(String path) {
        return requests.requestDeleteFolder(tokenKeeper.getToken(), path)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.DeleteFolderResult, Object>() {
                    @Override
                    public Object apply(@NonNull API.DeleteFolderResult res) throws Exception {
                        checkError(res);
                        return res;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();
    }

    @Override
    public Completable deleteFile(String path) {
        return requests.requestDeleteFile(tokenKeeper.getToken(), path)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.MetadataContainer, Object>() {
                    @Override
                    public Object apply(@NonNull API.MetadataContainer res) throws Exception {
                        checkError(res);
                        return res.metadata;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();
    }

    @Override
    public Observable<Integer> downloadFile(String remotePath, final String localPath) {
        Observable chain=
                requests.requestFileLink(tokenKeeper.getToken(), remotePath, 1) // gets download link
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        //.concatWith(Observable.<API.Link>never()) // get rid of "onComplete"
                        .flatMap(new Function<API.DownloadHosts, Observable<Integer>>(){
                            @Override
                            public Observable<Integer> apply(@NonNull API.DownloadHosts link) throws Exception {
                                checkError(link);

                                String url="https://"+link.hosts.get(0) + link.path;
                                return doDownloadResource(url, localPath);
                            }
                        })


                        .observeOn(AndroidSchedulers.mainThread());

        return chain;
    }

    @Override
    public Observable<Integer> uploadFile(String remotePath, String localPath) {
        return doUploadResource(remotePath, localPath);

    }


    @Override
    protected Observable doDownloadPartRequest(String link, String httpHdrRange) {
        return convertorlessRequests.requestPartialDownloadURL(  link, tokenKeeper.getToken(), httpHdrRange)
                      .toObservable();
    }

    @Override
    protected Single doUploadRequest(String link, RequestBody body) {
        File file=new File(link);

        return requests.requestUploadFile(tokenKeeper.getToken(), file.getParent(), file.getName(), body)
                .map(new Function<API.MetadataListContainer, Object>() {
                    @Override
                    public Object apply(@NonNull API.MetadataListContainer o) throws Exception {
                        checkError(o);
                        return o;
                    }
                });
    }
}
