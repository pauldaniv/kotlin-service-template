package com.pauldaniv.template.client;

import com.pauldaniv.template.request.TestOne;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestClient {

    @GET("/{isbn}")
    TestOne findByIsbn(@Query("isbn") String isbn);

//    @RequestLine("POST")
//    @Headers("Content-Type: application/json")
//    void create(TestOne book);
}
