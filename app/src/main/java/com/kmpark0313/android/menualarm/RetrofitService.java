package com.kmpark0313.android.menualarm;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

//서버에 get요청하는 인터페이스(자바의 인터페이스 어떠한 규격을 정해준다)
public interface RetrofitService {

    @GET("todayMenu.php")
    Call<RetrofitRepo> getMenu(
            @Query("day") String day
    );
}
