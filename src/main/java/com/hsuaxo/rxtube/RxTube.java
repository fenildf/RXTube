package com.hsuaxo.rxtube;

import com.google.gson.Gson;
import io.reactivex.Single;
import java.io.IOException;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Interceptor.Chain;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RxTube {

    final static Gson gson = new Gson();

    private final static String YT_API_URL = "https://www.googleapis.com/youtube/v3/";
    private final static String YT_API_KEY_PARAM = "key";
    private final static int YT_DEFAULT_MAX_RECORDS = 25;

    private final String key;
    private final Retrofit retrofit;
    private final YTService service;

    private OkHttpClient httpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request original = chain.request();
                final HttpUrl originalHttpUrl = original.url();
                final HttpUrl url = originalHttpUrl.newBuilder().addQueryParameter(YT_API_KEY_PARAM, key).build();
                final Request.Builder requestBuilder = original.newBuilder().url(url);
                final Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        return httpClient.build();
    }

    public RxTube(final String apiKey) {
        key = apiKey;
        retrofit = new Retrofit.Builder()
                .baseUrl(YT_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient())
                .build();
        service = retrofit.create(YTService.class);
    }

    public Single<YTResult> search(YTContentType type, String searchText, int maxRecords) {
        return service.search(type.toString(), searchText, maxRecords);
    }

    public Single<YTResult> search(YTContentType type, String searchText) {
        return search(type, searchText, YT_DEFAULT_MAX_RECORDS);
    }

    public Single<YTResult> searchVideos(String searchText, int maxRecords) {
        return search(YTContentType.VIDEO, searchText, maxRecords);
    }

    public Single<YTResult> searchVideos(String searchText) {
        return searchVideos(searchText, YT_DEFAULT_MAX_RECORDS);
    }

    public Single<YTResult> searchChannels(String searchText, int maxRecords) {
        return search(YTContentType.CHANNEL, searchText, maxRecords);
    }

    public Single<YTResult> searchChannels(String searchText) {
        return searchChannels(searchText, YT_DEFAULT_MAX_RECORDS);
    }

    public Single<YTResult> searchPlaylists(String searchText, int maxRecords) {
        return search(YTContentType.PLAYLIST, searchText, maxRecords);
    }

    public Single<YTResult> searchPlaylists(String searchText) {
        return searchPlaylists(searchText, YT_DEFAULT_MAX_RECORDS);
    }
}
