package com.boti.filescomparator.feign.client;

import com.boti.filescomparator.feign.fallback.MicrosoftGraphClientFallbackFactory;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

@Component
@FeignClient(name = "${graphMicrosoft.service.name}", url = "${graphMicrosoft.host}", path = "${graphMicrosoft.tenant}",
        fallback = MicrosoftGraphClientFallbackFactory.class)

public interface MicrosoftClient {
    @GetMapping("/drive/items/01WDX6HQH3SVA665HQZNA2EZGXRYRDFMSK/content")
    String getFile(@RequestHeader(value = "Authorization", required = true) String authorizationHeader);

}
