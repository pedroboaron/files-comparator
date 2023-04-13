package com.boti.filescomparator.dto;

import com.azure.core.annotation.Get;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemComparacao {
    private String item;
    private boolean statusAprovado;
    private String desc;
}
