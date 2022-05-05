package com.aleskovacic.eventslocator4.network;

import com.aleskovacic.eventslocator4.pojo.DataResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DataService {
    @GET("index.php")
    Call<DataResponse> getEvents(@Query("time") int time);

    @GET("search.php")
    Call<DataResponse> search(@Query("query") String query);
}