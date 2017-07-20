package com.beta.fbinvite.invite;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/7/18.
 */

public interface LoginAPIService {
    @GET("{id}")
    Observable<UrlModel> getUrl(@Path("id") String id, @Query("fields") String fields);
}
