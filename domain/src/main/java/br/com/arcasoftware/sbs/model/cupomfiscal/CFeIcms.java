package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFeIcms {
    private Long id;
    private String userCreate;
    private CFeItem cFeItem;
    private int origem;
    private String cst;
    private double percentual;
    private double valor;
}
