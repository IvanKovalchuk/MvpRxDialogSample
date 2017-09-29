package com.kivsw.cloud.disk.pcloud;

import com.kivsw.cloud.disk.IDiskIO;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * contains methods REST API methods for PCloud.com
 * https://docs.pcloud.com/methods/
 */

public class API {
    private static Retrofit retrofit,convertlessRetrofit;
    static synchronized private Retrofit getRetrofitInstance()
    {
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.pcloud.com:443")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    static synchronized private Retrofit getConvertlessRetrofitInstance()
    {
        if(convertlessRetrofit==null) {
            convertlessRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.pcloud.com:443")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return convertlessRetrofit;
    };

    static class ErrorInfo
    {
        public long result;
        public String error;
    }
/**
*  retrieves user information
* */
/** {
	"emailverified": true,
	"plan": 0,
	"cryptolifetime": false,
	"cryptosubscription": false,
	"cryptosetup": false,
	"quota": 10737418240,
	"userid": 10213067,
	"result": 0,
	"premium": false,
	"publiclinkquota": 53687091200,
	"usedquota": 56439004,
	"language": "ru",
	"business": false,
	"email": "ivan_kov@inbox.ru",
	"registered": "Fri, 11 Aug 2017 09:55:46 +0000"
}*/
    static public class UserInfo
        extends ErrorInfo
        implements IDiskIO.DiskInfo
    {
        public long     userid;
        public String email;
        public boolean emailverified, business, cryptolifetime, cryptosubscription, cryptosetup;
        public String registered;
        public boolean premium;
        public String premiumexpires;
        public long quota;
        public long usedquota;
        public String language;
        public long plan, publiclinkquota;
        public String auth;

        @Override
        public long size() {
            return quota;
        }

        @Override
        public long freeSize() {
            return quota - usedquota;
        }

        @Override
        public long used() {
            return usedquota;
        }
    };


    /**
     * file/folder metadata
     *
     */

    static public class Metadata
        implements IDiskIO.ResourceInfo
    {
        public long parentfolderid;
        public boolean isfolder,
                ismine,
                isshared;
        public String  name,
                id, folderid,fileid,
                deletedfileid,
                created,
                modified,
                icon;
        public int category; //    0 - uncategorized    1 - image    2 - video    3 - audio    4 - document    5 - archive
        public long size; // only for a file
        public String contenttype;
        public String hash;
        public List<Metadata> contents; // directory content
        public boolean isdeleted;
        public String path;

        @Override
        public long size() {
            return size;
        }

        @Override
        public boolean isFolder() {
            return isfolder;
        }

        @Override
        public boolean isFile() {
            return !isfolder;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public List<IDiskIO.ResourceInfo> content() {
            return (List<IDiskIO.ResourceInfo>) (List) contents;
        }

        @Override
        public long modified() {
            return strToDate(modified);
        }

        @Override
        public long created() {
            return strToDate(created);
        }

        private long strToDate(String dateStr)
        {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US); //Thu, 19 Sep 2013 07:31:46 +0000
                return sdf.parse(dateStr).getTime();
            }catch(Exception e)
            {
                return 0;
            }
        };
    }
    static public class MetadataContainer
            extends ErrorInfo
    {

        String id;
        public Metadata metadata;
    }
    static public class MetadataListContainer
            extends ErrorInfo
    {

        public List<String> fileids;
        public List<Metadata> metadata;
    }
    static public class DeleteFolderResult
            extends ErrorInfo {
        public long deletedfiles,
                    deletedfolders;
    }

    static public class DownloadHosts
            extends ErrorInfo {
        public String path,  expires;
        public List<String> hosts;
    }
    static public class UploadInfo
            extends ErrorInfo {
        public long uploadlinkid;
        public String link,  mail, code;

    }
    /**
     * creates requests for API
     */
    public interface Requests {
        @GET("/userinfo")
        Single<UserInfo> requestUserInfo(@Query("access_token") String oAuthToken);

        @GET("/listrevisions")
        Single<Object> requestListRevisions(@Query("access_token") String oAuthToken, @Query("path") String path);

        @GET("/listfolder")
        Single<MetadataContainer> requestListFolder(@Query("access_token") String oAuthToken, @Query("path") String path);

        @GET("/renamefile")
        Single<MetadataContainer> requestRenameFile(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("topath") String newpath);

        @POST("/createfolder") // it's possible to use @GET as well
        Single<MetadataContainer> requestCreateFolder(@Query("access_token") String oAuthToken, @Query("path") String path);

        @POST("/renamefolder")
        Single<MetadataContainer> requestRenameFolder(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("topath") String topath);

        @POST("/deletefolderrecursive")
        Single<DeleteFolderResult>  requestDeleteFolder(@Query("access_token") String oAuthToken, @Query("path") String path); // folder must be empty

        @POST("/deletefile")
        Single<MetadataContainer>  requestDeleteFile(@Query("access_token") String oAuthToken, @Query("path") String path); //

        @GET("/getfilelink")
        Single<DownloadHosts>  requestFileLink(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("forcedownload")int forcedownload); // https://docs.pcloud.com/methods/streaming/getfilelink.html

        @POST("/uploadfile")
        Single<MetadataListContainer>  requestUploadFile(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("filename") String filename, @Body RequestBody data); //https://docs.pcloud.com/methods/file/uploadfile.html

    }
    public interface ConvertorlessRequests{
        @GET("/userinfo")
        Single<Response<ResponseBody>> requestUserInfo(@Query("getauth") int getauth, @Query("access_token") String OAuthToken);

      /*  @GET("/listrevisions")
        Single<Response<ResponseBody>> requestListRevisions(@Query("access_token") String oAuthToken, @Query("path") String path);*/

        @GET("/listfolder")
        Single<Response<ResponseBody>> requestListFolder(@Query("access_token") String oAuthToken, @Query("path") String path);

        @GET("/renamefile")
        Single<Response<ResponseBody>> requestRenameFile(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("topath") String newpath);

        @GET("/createfolder")
        Single<Response<ResponseBody>> requestCreateFolder(@Query("access_token") String oAuthToken, @Query("path") String path);

        @GET("/renamefolder")
        Single<Response<ResponseBody>>  requestRenameFolder(@Query("access_token") String oAuthToken, @Query("path") String path, @Query("topath") String topath);

        @GET("/deletefolderrecursive")
        Single<Response<ResponseBody>>  requestDeleteFolder(@Query("access_token") String oAuthToken, @Query("path") String path);

        @GET
        Single<Response<ResponseBody>> requestDownloadURL(@Url String url, @Query("access_token") String oAuthToken,  @Header("Range") String range);

    };

    public static Requests createRequests()
    {
        return getRetrofitInstance().create(Requests.class);
        //return getConvertlessRetrofitInstance().create(RequestUserInfo_.class);
    };
    public static ConvertorlessRequests createConvertorlessRequests()
    {
        return getRetrofitInstance().create(ConvertorlessRequests.class);

    };


}
