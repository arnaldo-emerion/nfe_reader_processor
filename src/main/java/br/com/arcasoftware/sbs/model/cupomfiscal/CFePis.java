package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFePis {
    private Long id;
    private String userCreate;
    private CFeItem cFeItem;
    private String cst;
    private double percentual;
    private double valor;
}
