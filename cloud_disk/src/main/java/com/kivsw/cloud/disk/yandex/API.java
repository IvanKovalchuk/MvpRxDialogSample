package com.kivsw.cloud.disk.yandex;

import android.net.Uri;

import com.kivsw.cloud.disk.IDiskIO;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;


/**
 * Yandex disk API
 */
//https://tech.yandex.ru/disk/poligon/#!//v1/disk
    //https://tech.yandex.ru/disk/api/concepts/quickstart-docpage/
    //http://square.github.io/retrofit/
    //https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#single
    //https://futurestud.io/tutorials/retrofit-2-how-to-use-dynamic-urls-for-requests
public class API {

    private static Retrofit retrofit,convertlessRetrofit;
    static synchronized private Retrofit getRetrofitInstance()
    {
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://cloud-api.yandex.net:443")
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
                    .baseUrl("https://downloader.dst.yandex.ru")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return convertlessRetrofit;
    };
    /**
     * Error message from Yandex API
     */
 /*   public class ErrorDescription {
        String  message,     // <Человекочитаемое описание ошибки>,
                description, //<Техническое описание ошибки>,
                error;       //<Уникальный код ошибки>
    }*/

    /**
     * retrieve disk information
     */
    static public class Disk
            implements IDiskIO.DiskInfo
    {
        long max_file_size,// (integer, optional): <Максимальный поддерживаемый размер файла.>,
            total_space,// (integer, optional): <Общий объем диска (байт)>,
            trash_size;// (integer, optional): <Общий размер файлов в Корзине (байт). Входит в used_space.>,
        boolean is_paid;// (boolean, optional): <Признак наличия купленного места.>,
        long    used_space;// (integer, optional): <Используемый объем диска (байт)>,
        SystemFolders system_folders;// (SystemFolders, optional): <Адреса системных папок в Диске пользователя.>,
        long revision;// (integer, optional): <Текущая ревизия Диска>

        @Override
        public long size()
        {
            return total_space;
        };

        @Override
        public long freeSize() {
            return total_space-used_space;
        }

        @Override
        public long used() {
            return used_space;
        }
    };
    static public class SystemFolders
    {
        String  odnoklassniki,// (string, optional): <Путь к папке "Социальные сети/Одноклассники".>,
                google,// (string, optional): <Путь к папке "Социальные сети/Google+".>,
                instagram,// (string, optional): <Путь к папке "Социальные сети/Instagram".>,
                vkontakte,// (string, optional): <Путь к папке "Социальные сети/ВКонтакте".>,
                mailru,// (string, optional): <Путь к папке "Социальные сети/Мой Мир".>,
                downloads,// (string, optional): <Путь к папке "Загрузки".>,
                applications,// (string, optional): <Путь к папке "Приложения".>,
                facebook,// (string, optional): <Путь к папке "Социальные сети/Facebook".>,
                social,// (string, optional): <Путь к папке "Социальные сети".>,
                screenshots,// (string, optional): <Путь к папке "Скриншоты".>,
                photostream;// (string, optional): <Путь к папке "Фотокамера".>
    };

    public interface RequestDiskInfo{
        @GET("/v1/disk")
        Single<Disk> request(@Header("Authorization") String OAuthToken);
    }

    public static RequestDiskInfo createRequestDiskInfo()
    {return getRetrofitInstance().create(API.RequestDiskInfo.class);};


    /**
     *  get a resource info
     */
    static public class ResourceItem implements IDiskIO.ResourceInfo
    {
        public  String  name,
                    public_key,
                    created,
                    path,
                    type,
                    modified;
        public long size;
        public ResourceList _embedded;

        @Override
        public long size() {
            return size;
        }

        @Override
        public boolean isFolder() {
            return type.equals("dir");
        }

        @Override
        public boolean isFile() {
            return !isFolder();
        }

        @Override
        public String name() {
            if(path.equals("disk:/")) // I suppose that Yandex service has a bug
                return "/";           // it's workaround for the bug

            return name;
        }

        @Override
        public List<IDiskIO.ResourceInfo> content() {
            if(_embedded==null || _embedded.items==null)
                return null;
            return (List<IDiskIO.ResourceInfo>) (List)_embedded.items;
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ"); //2014-04-22T10:32:49+04:00
                return sdf.parse(dateStr).getTime();
            }catch(Exception e)
            {
                return 0;
            }
        };
    }
    static public class ResourceList
    {
        public String sort, path, limit, offset;
        public List<ResourceItem> items;
    }



    public interface RequestResourceInfo{
        @GET("/v1/disk/resources")
        Single<ResourceItem> request(@Header("Authorization") String OAuthToken, @Query("path") String path, @Query("offset") long offset, @Query("limit") long limit);
    }
    public static RequestResourceInfo createRequestResourceInfo()
    {return getRetrofitInstance().create(API.RequestResourceInfo.class);};

    /**
     * Create a new directory
     */
    static public class Link{
        String href,
               method;
        boolean templated;

        public String getId() // return ID out of href or null
        {
            if(href==null) return null;
            Uri uri=Uri.parse(href);
            String id=uri.getQueryParameter("id");
            if(id==null)
            {
                List<String> segments=uri.getPathSegments();
                if(segments.contains("operations"))
                    id = uri.getLastPathSegment();
            };
            return id;
        };


    }
    public interface RequestCreatingDir{
        @PUT("/v1/disk/resources")
        Single<Link> request(@Header("Authorization") String OAuthToken, @Query("path") String path);
    }
    public static RequestCreatingDir createRequestCreatingDir()
    {
        return getRetrofitInstance().create(API.RequestCreatingDir.class);
    };

    /**
     * move file or directory
     */
    public interface RequestMoving{
        @POST("/v1/disk/resources/move")
        Single<Link> request(@Header("Authorization") String OAuthToken, @Query("from") String from, @Query("path") String newName, @Query("overwrite") boolean overwrite);
    }
    public static RequestMoving createRequestMoving()
    {
        return getRetrofitInstance().create(API.RequestMoving.class);
    };

    /**
     * deletes a file or directory
     */
    public interface RequestDelete{
        @DELETE("/v1/disk/resources")
        Maybe<Link> request(@Header("Authorization") String OAuthToken, @Query("path") String newName, @Query("permanently") boolean permanently);
    }
    public static RequestDelete createRequestDelete()
    {
        return getRetrofitInstance().create(API.RequestDelete.class);
    };

    /**
     * gets an operation status
     */
    static public class Operation{
        String status;
        public boolean isSuccess(){return "success".equals(status);}
    }

   /* public interface RequestOperationStatus{
        @GET("v1/disk/operations/{operation_id}")
        Single<Operation> request(@Header("Authorization") String OAuthToken, @Path("operation_id") String id);
    };*/

    public interface RequestOperationStatusUrl{
        @GET
        Single<Operation> request(@Header("Authorization") String OAuthToken, @Url String url);
    };
    public static RequestOperationStatusUrl createRequestOperationStatusUrl()
    {
        return getRetrofitInstance().create(API.RequestOperationStatusUrl.class);
    };
    /**
     * gets file link for downloading
     */
    public interface RequestDownloadLink{
        @GET("/v1/disk/resources/download")
        Single<Link> request(@Header("Authorization") String OAuthToken, @Query("path") String path);
    };
    public static RequestDownloadLink createRequestDownloadLink()
    {
        return getRetrofitInstance().create(API.RequestDownloadLink.class);
    };

    /**
     * download a file
     */
    public interface RequestDownload{
        @GET
        Single<retrofit2.Response<ResponseBody>> request(@Header("Authorization") String OAuthToken, @Url String url);
    };
    public static RequestDownload createRequestDownload()
    {
        return getConvertlessRetrofitInstance().create(API.RequestDownload.class);
    };

    public interface RequestPartialDownload{
        @GET
        Single<Response<ResponseBody>> request(@Header("Authorization") String OAuthToken, @Url String url, @Header("Range") String range);
    };
    public static RequestPartialDownload createRequestPartialDownload()
    {
        return getConvertlessRetrofitInstance().create(API.RequestPartialDownload.class);
    };

    /**
     * gets filelink to upload
     */
    public interface RequestUploadLink{
        @GET("/v1/disk/resources/upload")
        Single<Link> request(@Header("Authorization") String OAuthToken, @Query("path") String path, @Query("overwrite") boolean overwrite);
    };
    public static RequestUploadLink createRequestUploadLink()
    {
        return getRetrofitInstance().create(API.RequestUploadLink.class);
    };
    /**
     * Upload a file
     */
    public interface RequestUpload {
        @PUT
        Single<Response<ResponseBody>> request(@Url String url, @Body RequestBody data);
    };
    public static RequestUpload createRequestUpload()
    {
        return getConvertlessRetrofitInstance().create(RequestUpload.class);
    };

}
