package com.boti.filescomparator.feign.fallback;

import com.boti.filescomparator.feign.client.MicrosoftClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;

@Slf4j
public class MicrosoftGraphClientFallbackFactory implements FallbackFactory<MicrosoftClient> {

    @Override
    public MicrosoftClient create(Throwable cause) {
        return new MicrosoftClient() {
            @Override
            public String getFile(String authorizationHeader) {
                log.error("fallback; get match-keywords reason was: " + cause.getMessage() );
                return null;
            }
        };
    }
}
