package com.giaothuy.ebookone.api;

import com.giaothuy.ebookone.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by 1 on 3/7/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/user")
    Call<ServerResponse> createUser(@Field("uid") String uid, @Field("name") String name, @Field("email") String email, @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("api/comment")
    Call<ServerResponse> addComment(@Field("uid") String uid, @Field("message") String message);


}
