package com.kmpark0313.android.menualarm;

import retrofit2.Call;
import retrofit2.http.GET;

//서버에 get요청하는 인터페이스(자바의 인터페이스 어떠한 규격을 정해준다)
public interface RetrofitService2 {


    @GET("version.php")
    Call<RetrofitRepo2> getVersion();
}
