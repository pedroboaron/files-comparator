package com.boti.filescomparator.dto;

import com.azure.core.annotation.Get;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemComparacao {
    private String item;
    private boolean statusAprovado;
    private String desc;

    public ItemComparacao(String item) {
        this.item = item;
    }
}
