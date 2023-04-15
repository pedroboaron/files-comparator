package com.boti.filescomparator.feign.client;

import com.boti.filescomparator.dto.cadastroEmpresa.EmpresaDtoResponse;
import com.boti.filescomparator.feign.fallback.CadstroEmpresaClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "${cadastroEmpresa.service.name}", path = "${cadastroEmpresa.path}",
        fallback = CadstroEmpresaClientFallbackFactory.class)

public interface CadastroEmpresaClient {
    @GetMapping("/empresa/{id}")
    EmpresaDtoResponse getEmpresaById(@PathVariable("id") Integer id);

}
