package com.boti.filescomparator.feign.fallback;

import com.boti.filescomparator.dto.cadastroEmpresa.EmpresaDtoResponse;
import com.boti.filescomparator.feign.client.CadastroEmpresaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
public class CadstroEmpresaClientFallbackFactory implements FallbackFactory<CadastroEmpresaClient> {

    @Override
    public CadastroEmpresaClient create(Throwable cause) {
        return new CadastroEmpresaClient() {
            @Override
            public EmpresaDtoResponse getEmpresaById(Integer id) {
                log.error("fallback; get empresa by id reason was: " + cause.getMessage() );
                return null;
            }
        };
    }
}
