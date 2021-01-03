package com.example.chatapp21.FragmentS;

import com.example.chatapp21.Notifications.MyResoureces;
import com.example.chatapp21.Notifications.sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServies {
    @Headers({
            "Content-Type:Appliction/json",
            "Authorization:key = AAAA1rte214:APA91bHAHMRfFwAmxGBogSrAuVzIZB0LHOIMm4RPK4ka5UZqb93hUEyHkPtX34w0GJi28lbvuY2g99ZqSqn6nB4uHscXDyaOSIDrf7G6hngxuLF58nkuqfEb5Ek8lQJuT81IAZ-A5lPT"
    })
    @POST("fcm/send")
    Call<MyResoureces>sendNotification(@Body sender body );
}
