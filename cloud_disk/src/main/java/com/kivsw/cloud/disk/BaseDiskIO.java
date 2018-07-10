package com.kivsw.cloud.disk;

import android.content.Context;

import com.google.gson.Gson;
import com.kivsw.cloud.OAuth.BaseIOAuthCore;
import com.kivsw.cloud.OAuth.OAuthActivityContract;
import com.kivsw.cloud.OAuth.OAuthPresenter;
import com.kivsw.cloud.disk.helpers.TokenKeeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by ivan on 7/20/17.
 */

public abstract class BaseDiskIO implements IDiskIO {

    protected TokenKeeper tokenKeeper;
    Context context;

    public BaseDiskIO(Context context)
    {
        this.context = context.getApplicationContext();
        tokenKeeper = TokenKeeper.getInstance(context, this.getClass().getName());
    }

    public abstract BaseIOAuthCore createIOAuthCore();

    public Completable authorize()
    {
        OAuthActivityContract.IPresenter presenter = OAuthPresenter.getInstance(context);
        //final YandexOAuthCore.newInstance(clientId, null, deviceId, deviceName)
        final BaseIOAuthCore core=createIOAuthCore();
        presenter.startAuthorization(core);

        Single<OAuthActivityContract.IOAuthCore> single=
                Single.fromObservable(core.getObservable().take(1))
                .map(new Function<OAuthActivityContract.IOAuthCore,OAuthActivityContract.IOAuthCore>() {

                    @Override
                    public OAuthActivityContract.IOAuthCore apply(@NonNull OAuthActivityContract.IOAuthCore o) throws Exception {
                        String token=core.getToken();
                        if(token!=null) {
                            tokenKeeper.setToken(token);

                            String expiredIn = core.getExpiredIn();
                            if (expiredIn != null)
                                try {
                                    tokenKeeper.setExpiredIn(Long.parseLong(expiredIn));
                                } catch (Exception e) {
                                    e.toString();
                                }
                            ;

                            String refreshToken = core.getRefreshToken();
                            if (refreshToken != null)
                                tokenKeeper.setRefreshToken(refreshToken);
                        }
                        else
                        {
                            StringBuilder msg=new StringBuilder();
                            msg.append(core.getErrCode());

                            msg.append(" (");
                            msg.append(core.getErrorDesc() );
                            msg.append(")");

                            OAuthActivityContract.IOAuthException ex=new OAuthActivityContract.IOAuthException(msg.toString());

                            throw ex;
                        }

                        return core;
                    };

                });


        return single.toCompletable();
        //return Single.fromObservable(observable);
    }

    public static class NoAuthorizedException extends Exception
    {
        public NoAuthorizedException(){super();};
        public NoAuthorizedException(String msg){super(msg);};
    }
    @Override
    public Completable isAuthorized()
    {
        if(tokenKeeper.getToken() == null)
            return Completable.error(new NoAuthorizedException());

        return getRequestDiskInfo()
                .doOnSuccess(new Consumer<DiskInfo>(){
                    @Override
                    public void accept(@NonNull DiskInfo diskInfo) throws Exception {
                        diskInfo.size();
                    }
                })
                .toCompletable();
    };

    @Override
    public Completable authorizeIfNecessary() {

        Completable completable = isAuthorized()
                .onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Throwable throwable) throws Exception {
                        if ( (throwable instanceof HttpException) ||
                             (throwable instanceof NoAuthorizedException)   )
                            return authorize();
                        throw new Exception(throwable);
                    };

                });

        return completable;
    }
    static class JSON_Error
    {
         String message,
                description,
                error;
        //JSON_Error(){};
    };
    @Override
    public String getErrorString(Throwable e)
    {
        return errorToString(e);
    }
    public static String errorToString(Throwable e)
    {

        if(e instanceof CompositeException)
        {
            List<Throwable> list=((CompositeException)e).getExceptions();
            StringBuilder result=new StringBuilder();
            for(Throwable t:list) {
                result.append(errorToString(t));
                result.append("\n");
            };

            return result.toString();
        }

        String s=null;
        if(e instanceof HttpException) {
            try {
                Gson gson = new Gson();
                String errJson =  ((HttpException) e).response().errorBody().string();

                JSON_Error err =  gson.fromJson(errJson, JSON_Error.class);
                StringBuilder stringbuilder = new StringBuilder();
                if(err.error!=null) {
                    stringbuilder.append(err.error);
                    stringbuilder.append("\n");
                }
                if(err.description!=null) {
                    stringbuilder.append(err.description);
                    stringbuilder.append("\n");
                }
                if(err.message!=null) {
                    stringbuilder.append(err.message);
                    stringbuilder.append("\n");
                }
                s = stringbuilder.toString();
            } catch (Exception ex) {
            };
        }

        if(s==null)
            s=e.toString();

        return s;
    };

    /**
     *  download a resource
     */
    static private class FilePart
    {
        long fragmentBegin, fragmentEnd ,fileSize;
        String link;

        OutputStream outputStream;
        InputStream inputStream;
        FilePart(String lnk, long p)
        {
            link=lnk; fragmentBegin=fragmentEnd=p;
        }
        FilePart()
        {
            link=null;
            fragmentBegin=0;
            fragmentEnd=0;
        }
    }
    protected final long maxFragmentLength=60*1024;
    protected Observable<Integer> doDownloadResource(String url, final String localPath ) throws Exception
    {
        final FilePart filePart=new FilePart(url,0);
        filePart.outputStream = new FileOutputStream(localPath);
        filePart.fragmentEnd=1;

        final Subject<FilePart> filePosition= //PublishSubject.create();
                                             BehaviorSubject.createDefault(filePart);

        Observable<Integer> result=
                filePosition
                .flatMap((Function) new Function<FilePart, Observable>() {
                    @Override
                    public Observable apply(@NonNull FilePart filePart) throws Exception {

                        //String range = "bytes=" + String.valueOf(filePart.pos)+"-"+String.valueOf(filePart.pos+fragmentLength);
                        String range = "bytes=" + String.valueOf(filePart.fragmentBegin)+"-"+String.valueOf(filePart.fragmentEnd);
                        return doDownloadPartRequest(filePart.link, range);

                    }
                })

                .takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(@NonNull Object o) throws Exception {
                        Response<ResponseBody> response=(Response)o;

                        Headers hh=response.headers();
                        int code = response.code();
                        String range=hh.get("Content-Range");
                        long[] ranges = decodeRanges(range);


                        boolean res=false;
                        if(ranges!=null && (code==206))
                            res = (ranges.length<3) || (ranges[1]<(ranges[2]-1));

                        if((ranges!=null) && (filePart.fragmentBegin != ranges[0]))
                            throw new IOException("Incorrect file data from server");

                        if(res){
                            filePart.fileSize = ranges[2];
                            filePart.fragmentBegin = ranges[1]+1;
                            filePart.fragmentEnd = filePart.fragmentBegin+maxFragmentLength;
                            if(filePart.fragmentEnd>=filePart.fileSize)
                                   filePart.fragmentEnd=filePart.fileSize-1;

                            filePosition.onNext(filePart);
                        }

                        // saves data to file
                        if(response.body()!=null) {
                            byte[] data = response.body().bytes();

                            //if(ranges!=null) offset=ranges[0];
                            filePart.outputStream.write(data);
                        }

                        if(!res) {
                            filePart.outputStream.close();
                            if((code<200 || code >299)/* && code!=416*/)
                            {
                                throw new HttpException(response);
                            }
                        }


                        return res;
                    }
                })
                .doOnError(new Consumer(){
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        filePart.outputStream.close();
                    }
                })

                .map((Function)new Function<Response<ResponseBody>, Integer>(){

                            @Override
                            public Integer apply(@NonNull Response<ResponseBody> response) throws Exception {
                                Headers hh=response.headers();
                                String range=hh.get("Content-Range");
                                long[] ranges = decodeRanges(range);
                                return (int)(ranges[1]*100/ranges[2]);

                            }
                        }
                )
                .distinctUntilChanged() // suppresses the same values
                .debounce(500, TimeUnit.MICROSECONDS); // drops item if they are too frequent

        //filePosition.onNext(filePart);
        return result;
    }

    /**
     *
     * @param link a resource link to download
     * @param httpHdrRange data range in http-header format : bytes=xxx-eee
     *                     xxx-the first byte, eee-the last byte
     * @return
     */
    protected abstract Observable doDownloadPartRequest(String link, String httpHdrRange);
    /**
     *
     * @param contentRange
     * @return array that contains first, last bytes and total file size
     */
    protected long[] decodeRanges(String contentRange)
    {
        try {
            String[] ranges = contentRange.split("[ -/]");
            long[] res = new long[ranges.length-1];
            for (int i = 1; i < ranges.length; i++)
                res[i-1] = Long.parseLong(ranges[i]);

            return res;
        }catch(Exception e)
        {
            return null;
        }
    }


    protected abstract Single doUploadRequest(String link, RequestBody body);
    protected Observable<Integer> doUploadResource(String url, final String localPath )
    {
        //RequestBody body = RequestBody.create(MediaType.parse("application/octet-stream"), new File(localPath));
        FileRequestBody body = new FileRequestBody(localPath);

        return Observable.merge(doUploadRequest(url, body).subscribeOn(Schedulers.io()).toObservable(), body.getProgressObservable()  )

                .takeWhile(new Predicate() {
                    @Override
                    public boolean test(@NonNull Object o) throws Exception {
                        if(o instanceof Integer)
                           return true;

                        if(o instanceof Response) {
                            Response<ResponseBody> response = (Response<ResponseBody>) o;
                            Headers hh = response.headers();
                            int code = response.code();
                            if (code < 200 || code > 299) {
                                throw new HttpException(response);
                            }
                        }
                        return false;
                    }
                });


    };

    @Override
    public boolean isLocalStorage()
    {
        return false;
    };
    @Override
    public String convertToLocalPath(String path)
    {return null;};

    /**
     * This class create request body out of a file
     */
    static protected class FileRequestBody extends RequestBody
    {
        private MediaType mediaType;
        private File file;
        private long readCount, fileLength;
        private int percent=0;
        private Subject<Integer> progressObservable;
        public FileRequestBody(String localFile)
        {
            mediaType = MediaType.parse("application/octet-stream");
            file = new File(localFile);
            fileLength = file.length();
            readCount = 0;
            progressObservable = PublishSubject.create();
            //progressObservable = BehaviorSubject.create();
        }

        @Override public long contentLength() {
            return file.length();
        }
        @Nullable
        @Override
        public MediaType contentType() {
            return mediaType;
        };

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            try {
                //source = Okio.source(file);
                readCount=0;
                source = Okio.source(new FileInputStream(file){
                    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException
                    {
                        int r=super.read(buffer, byteOffset, byteCount);
                        if(r!=-1) readCount+=r;
                        int p=0;
                        if(fileLength!=0)
                            p=(int)(100*readCount / fileLength);
                        if(p!=percent) {
                            percent = p;
                            progressObservable.onNext(Integer.valueOf(percent));
                        }
                        return r;
                    }

                });

                sink.writeAll(source);
            } finally {
                Util.closeQuietly(source);
            }
        };

        Observable<Integer> getProgressObservable()
        {
            return progressObservable
                .distinctUntilChanged() // suppresses the save values
                .debounce(500, TimeUnit.MICROSECONDS); // drops item if they are too frequent;
        }
    }


}
