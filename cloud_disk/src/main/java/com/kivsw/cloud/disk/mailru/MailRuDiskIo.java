package com.kivsw.cloud.disk.mailru;

import android.content.Context;

import com.kivsw.cloud.OAuth.BaseIOAuthCore;
import com.kivsw.cloud.OAuth.MailRuOAuthCore;
import com.kivsw.cloud.disk.BaseDiskIO;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

//import com.kovalchuk.ivan.testoauth.cloud_disk.IHandler;


/**
 * Created by ivan on 6/27/17.
 */
//https://habrahabr.ru/post/281360/
public class MailRuDiskIo extends BaseDiskIO {

    String clientId;

    API.RequestSpace requestSpace;
    public MailRuDiskIo(Context cnt, String clientId)
    {
        super(cnt);
        this.clientId  = clientId;
        initRetrofit();

    }
    void initRetrofit() {

        requestSpace = API.createRequestSpace();
    }

    public BaseIOAuthCore createIOAuthCore() {
        return MailRuOAuthCore.newInstance(clientId, null);
    }

    @Override
    protected Observable doDownloadPartRequest(String link, String httpHdrRange) {
        return null;
    }

    @Override
    protected Single doUploadRequest(String link, RequestBody body) {
        return null;
    }


    public Single getRequestDiskInfo() {
        String mail="kovalchuk_i_v@mail.ru";
        return
        requestSpace.request(2, mail,mail, tokenKeeper.getRefreshToken())
                .subscribeOn(Schedulers.io())
                .map(new Function<API.Space, API.Space>(){
                    @Override
                    public API.Space apply(API.Space disk) {// TODO transforming data format
                        return disk;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public Single getResourceInfo(String path) {
        return null;
    }

    @Override
    public Completable createDir(String path) {
        return null;
    }

    @Override
    public Completable renameDir(String path, String newPath) {
        return null;
    }

    @Override
    public Completable renameFile(String path, String newPath) {
        return null;
    }

    @Override
    public Completable deleteDir(String path) {
        return null;
    }

    @Override
    public Completable deleteFile(String path) {
        return null;
    }

    @Override
    public Observable<Integer> downloadFile(String remotePath, String localPath) {
        return null;
    }

    @Override
    public Observable<Integer> uploadFile(String remotePath, String localPath) {
        return null;
    }

   /* @Override
    public Completable waitForCompleted(Single single) {
        return null;
    }*/
}
