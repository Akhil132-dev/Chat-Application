package com.example.chatapp21.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class client {
    object  client{
        private  var retrofit:Retrofit?= null

        fun getclinet(url:String?): Retrofit? {
            if(retrofit == null){
                retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }
    }

}