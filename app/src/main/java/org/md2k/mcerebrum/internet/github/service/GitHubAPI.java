package org.md2k.mcerebrum.internet.github.service;


import org.md2k.mcerebrum.internet.github.model.ReleaseInfo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;


interface GitHubAPI {
    String SERVICE_ENDPOINT = "https://api.github.com/";

    @GET("repos/{owner}/{repo}/releases/latest")
    Observable<ReleaseInfo> getReleaseLatest(@Path("owner") String owner, @Path("repo") String repo);
    @GET("repos/{owner}/{repo}/releases")
    Observable<ReleaseInfo[]> getReleases(@Path("owner") String owner, @Path("repo") String repo);

}
