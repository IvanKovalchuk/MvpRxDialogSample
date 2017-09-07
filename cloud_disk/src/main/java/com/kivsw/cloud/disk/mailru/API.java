package com.kivsw.cloud.disk.mailru;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ivan on 8/9/17.
 */

public class API {
    private static Retrofit retrofit,convertlessRetrofit;
    static synchronized private Retrofit getRetrofitInstance()
    {
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://cloud.mail.ru:443")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * space size
     * https://cloud.mail.ru/api/v2/user/space?api=2&build=hotfix_CLOUDWEB-7669_46-0-1.201708091359&x-page-id=dxJPTSgvch&email=kovalchuk_i_v%40mail.ru&x-email=kovalchuk_i_v%40mail.ru&token=84gMr1GGnYBLcWHRqS1pN8TP6ph2sY1r&_=1502371193741
     */

    static public class Space
    {
        String email;
        long status,
             time;
        SpaceBody body;
    }
    static public class SpaceBody
    {
        boolean overquota;
        long used,
             total;
    }

    public interface RequestSpace{
        @GET("/api/v2/user/space")
        Single<Space> request(@Query("api") int api, @Query("email") String email, @Query("x-email") String xemail, @Query("token") String OAuthToken);
    }
    public static RequestSpace createRequestSpace()
    {
        return getRetrofitInstance().create(RequestSpace.class);
    }

}
