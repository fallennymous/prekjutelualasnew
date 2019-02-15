package com.example.fallennymous.prekjutelulas.Remote;

import com.example.fallennymous.prekjutelulas.Model.MyResponse;
import com.example.fallennymous.prekjutelulas.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fallennymous on 14/02/2019.
 */

public interface APIService {
    @Headers(
            {
                    "Content_Type:application/json",
                    "Authorization:key=AAAAlXjOb7Q:APA91bGtA-DKPExejXrk43d5ATRSc738GXYpvnngLp0uN3I1xhM4IMup4CW6rJTWkyNDtU4kvzbUWHTKmoqGoPVhkj2-czsWb2BK0nqvVUu3i4q9I7Gyy4Fp7t_ctSRTP6IO8bBVu0Lk"
            }

    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}