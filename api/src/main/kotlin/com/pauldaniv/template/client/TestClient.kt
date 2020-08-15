package com.pauldaniv.template.client

import com.pauldaniv.retrofit2.clients.Client
import com.pauldaniv.template.request.TestOne
import retrofit2.http.GET
import retrofit2.http.Query

@Client
interface TestClient {
  @GET("/{isbn}")
  fun findByIsbn(@Query("isbn") isbn: String): TestOne

//    @RequestLine("POST")
//    @Headers("Content-Type: application/json")
//    void create(TestOne book);
}
