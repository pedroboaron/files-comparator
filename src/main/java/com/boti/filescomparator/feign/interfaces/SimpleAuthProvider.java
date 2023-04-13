package com.boti.filescomparator.feign.interfaces;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.http.IHttpRequest;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class SimpleAuthProvider implements IAuthenticationProvider {

    private String accessToken = null;

    public SimpleAuthProvider(String accessToken) {
        this.accessToken = accessToken;
    }

    @NotNull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL url) {
        return CompletableFuture.completedFuture(this.accessToken);
    }
}