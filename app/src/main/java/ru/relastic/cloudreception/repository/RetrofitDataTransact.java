package ru.relastic.cloudreception.repository;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class RetrofitDataTransact implements DataTransact{
    //private static final String VERSION_API = "2.5/";
    public static final String BASE_URL = "http://cloudreception.relastic.ru/";
    public static final String PATH_FIRST = "/";

    private final RetrofitHelper mHelper;

    public RetrofitDataTransact() {
        this.mHelper = new RetrofitHelper();
    }


    @Override
    public void requestData(AuthData auth, final IDataTransactCallback callback) {
        Callback<TransactData> callbacks = new Callback<TransactData>() {
            @Override
            public void onResponse(@NonNull Call<TransactData> call, @NonNull Response<TransactData> response) {
                if (callback!=null) {callback.onResponseData(response.body());}
            }
            @Override
            public void onFailure(@NonNull Call<TransactData> call, @NonNull Throwable t) {
                //<...>
            }
        };

        this.mHelper.getService().sendGETRequest(
                auth.getType(),
                auth.getLogin(),
                auth.getSession_id(),
                auth.getToken(),
                auth.getArg(),
                auth.getExtras())
                .enqueue(callbacks);
    }


    public static TransactData convertJSON(String json_string) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json_string, TransactData.class);
    }
    public static String convertToString(TransactData object) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(object);
    }



    public static class RetrofitHelper {

        public static IRetrofitCallback getService() {
            Gson gson = new GsonBuilder().setLenient().create();


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build();

            /*
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            */
            return retrofit.create(IRetrofitCallback.class);
        }
    }
    public interface IRetrofitCallback {
        @Headers({"Content-Type: application/json"})
        @GET(RetrofitDataTransact.PATH_FIRST)
        Call<TransactData> sendGETRequest (@Query("type") long id,
                                           @Query("login") String login,
                                           @Query("session_id") String session_id,
                                           @Query("token") String token,
                                           @Query("arg") long arg,
                                           @Query("extras") String extras);

        @Headers({"Content-Type: application/json"})
        @POST(RetrofitDataTransact.PATH_FIRST)
        Call<TransactData> sendPOSTRequest (@Query("type") long id,
                                            @Query("login") String login,
                                            @Query("session_id") String session_id,
                                            @Query("token") String token,
                                            @Query("arg") long arg,
                                            @Query("extras") String extras);

    }
}