package com.boti.filescomparator.dto.cadastroEmpresa;

import lombok.*;

import java.util.Set;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDtoResponse {

    private Long id;

    private Integer codigo;

    private String razaoSocial;

    private String cnpj;

    private String grupo;

    private String regime;

    private String uf;

    private boolean icms;

    private boolean iss;

    private Set<SocioDtoResponse> socios;
}
