package com.kivsw.cloud.disk.yandex;

import android.content.Context;

import com.kivsw.cloud.OAuth.BaseIOAuthCore;
import com.kivsw.cloud.OAuth.YandexOAuthCore;
import com.kivsw.cloud.disk.BaseDiskIO;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


/**
 * Created by ivan on 6/27/17.
 */
//https://tech.yandex.ru/disk/rest/
//http://square.github.io/retrofit/2.x/retrofit/
//https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0
public class YandexDiskIo extends BaseDiskIO {

    private API.RequestDiskInfo requestDiskInfo;
    private API.RequestResourceInfo resourceInfo;
    private API.RequestCreatingDir requestCreatingDir;
    private API.RequestMoving requestMove;
    private API.RequestDelete requestDelete;
    //private API.RequestOperationStatus requestOperationStatus;
    private API.RequestOperationStatusUrl requestOperationStatusUrl;
    private API.RequestDownloadLink requestDownloadLink;
    //private API.RequestDownload requestDownload;
    private API.RequestPartialDownload requestPartialDownload;
    private API.RequestUploadLink requestUploadLink;
    private API.RequestUpload requestUpload;
    private final String clientId, deviceName, deviceId;


    public YandexDiskIo(Context cnt, String clientId, String deviceId, String deviceName) {
        super(cnt);

        this.clientId = clientId;
        this.deviceName = deviceName;
        this.deviceId = deviceId;


        initRetrofit();
    }
    void initRetrofit()
    {

        requestDiskInfo = API.createRequestDiskInfo();
        resourceInfo = API.createRequestResourceInfo();
        requestCreatingDir = API.createRequestCreatingDir();
        requestMove = API.createRequestMoving();
        requestDelete = API.createRequestDelete();
       // requestOperationStatus = retrofit.create(API.RequestOperationStatus.class);
        requestOperationStatusUrl = API.createRequestOperationStatusUrl();

        requestDownloadLink = API.createRequestDownloadLink();
        //requestDownload = API.createRequestDownload();
        requestPartialDownload = API.createRequestPartialDownload();

        requestUploadLink = API.createRequestUploadLink();
        requestUpload = API.createRequestUpload();
    }




    String tokenParam()
    {
        String t=tokenKeeper.getToken();//+"xx";
        if(t!=null)
            t = "OAuth "+t;
        return t;
    }

    @Override
    public BaseIOAuthCore createIOAuthCore()
    {
        return YandexOAuthCore.newInstance(clientId, null, deviceId, deviceName);
    };

    @Override
    public Single<DiskInfo> getRequestDiskInfo() {

        return requestDiskInfo.request(tokenParam())
                .subscribeOn(Schedulers.io())
                .map(new Function<API.Disk, DiskInfo >(){
                    @Override
                    public DiskInfo apply(API.Disk disk) {// TODO transforming data format

                        return disk;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<ResourceInfo> getResourceInfo(String path) {

        return
        resourceInfo.request(tokenParam(), path, 0, 1024*64)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.ResourceItem , ResourceInfo>(){
                    @Override
                    public ResourceInfo apply(API.ResourceItem info) {// TODO transforming data format

                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Completable createDir(String path) {

        return requestCreatingDir.request(tokenParam(), path)
                .subscribeOn(Schedulers.io())
                .map(new Function<API.Link , API.Link>(){
                    @Override
                    public API.Link apply(API.Link info) {// TODO transforming data format

                        return info;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .toCompletable();


    }

    @Override
    public Completable renameDir(String path, String newPath) {
        return renameFile(path, newPath);
    }

    @Override
    public Completable renameFile(String path, String newPath) {
        return
                waitForCompleted(requestMove
                            .request(tokenParam(), path, newPath, true)
                            .subscribeOn(Schedulers.io())
                )
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deleteDir(String path) {
        return deleteFile(path);
    }

    @Override
    public Completable deleteFile(String path) {


        return waitForCompleted( requestDelete
                                   .request(tokenParam(), path, true)
                                   .subscribeOn(Schedulers.io())
                               )
                .observeOn(AndroidSchedulers.mainThread());


    }

    public Completable waitForCompleted(Single single)
    {
        return waitForCompleted(single.toMaybe());
    }
    public Completable waitForCompleted(Maybe maybe) // Single<API.Link> single
    {
        Observable observable =
                maybe
                        //.observeOn(Schedulers.io())
                        .map(new Function<API.Link, String>() {
                            @Override
                            public String apply(API.Link info) {
                                String id = info.getId();
                                if (id == null)
                                    return "";
                                return info.href;
                            }
                        })
                        .toObservable()
                        .flatMap(new Function<String, Observable>() {
                            @Override
                            public Observable apply(@NonNull final String id) throws Exception { // periodically emmits id (if it's necessary) or complete
                                if (id == null || id.isEmpty())
                                    return Observable.empty(); // operation has finished

                                return Observable.interval(500, TimeUnit.MILLISECONDS)
                                        .map(new Function<Long, String>() {
                                            @Override
                                            public String apply(@NonNull Long o) throws Exception {
                                                return id;
                                            }
                                        });
                            }
                        })

                        .concatMap(new Function<String, Observable<API.Operation>>() {  // asks for operation status
                            @Override
                            public Observable<API.Operation> apply(@NonNull String id) throws Exception {
                                return requestOperationStatusUrl.request(tokenParam(), id).toObservable();
                            }
                        })
                        .takeWhile(new Predicate<API.Operation>(){
                            @Override
                            public boolean test(@NonNull API.Operation opResult) throws Exception {
                                return !opResult.isSuccess();
                            }
                        });

        //.observeOn(AndroidSchedulers.mainThread());

        return Completable.fromObservable(observable);



    }

    /**
     * creates an observer to download a piece of file
     * @param link a resource link to download
     * @param range define a piece of file in http-GET format
     * @return
     */

    @Override
    protected Observable doDownloadPartRequest(String link, String range)
    {
        return requestPartialDownload.request(tokenParam(), link, range).toObservable();
    };

    /**
     *  download a file
     * @param remotePath
     * @param localPath
     * @return Observable that generate complete progress
     */
    @Override
    public Observable<Integer> downloadFile(final String remotePath, final String localPath) {


       Observable chain=
                requestDownloadLink.request(tokenParam(), remotePath) // gets download link
                        .subscribeOn(Schedulers.io())
                        .toObservable()
                        //.concatWith(Observable.<API.Link>never()) // get rid of "onComplete"
                        .flatMap(new Function<API.Link, Observable<Integer>>(){
                            @Override
                            public Observable<Integer> apply(@NonNull API.Link link) throws Exception {
                                return doDownloadResource(link.href, localPath);
                            }
                        })


                        .observeOn(AndroidSchedulers.mainThread());

        return chain;

    }

    /**
     * creates an observer to download a piece of file
     * @param link a resource link to download
     * @param range define a piece of file in http-GET format
     * @return
     */

   /* @Override
    protected Observable doUploadPartRequest(String link, String range, byte[] data)
    {
        RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"),data);
        //RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"),data);

        return requestUpload.request(link, range, body).toObservable();
    };*/

    /**
     *  uploads a file
     * @param remotePath
     * @param localPath
     * @return Observable that generate complete progress
     */

    @Override
    public Observable<Integer> uploadFile(final String remotePath, final String localPath) {
        Observable chain=
                requestUploadLink.request(tokenParam(), remotePath, true) // gets download link
                        .subscribeOn(Schedulers.io())

                        .flatMapObservable(new Function<API.Link, Observable<Integer>>(){
                            @Override
                            public Observable<Integer> apply(@NonNull API.Link link) throws Exception {
                                return doUploadResource(link.href, localPath);

                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());

        return chain;
    }
    @Override
    protected Single doUploadRequest(String link, RequestBody body)
    {
        return requestUpload.request(link,  body);
    };


}
