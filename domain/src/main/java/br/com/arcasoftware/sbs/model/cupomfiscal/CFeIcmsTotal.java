package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFeIcmsTotal {
    private Long id;
    private String userCreate;
    private CupomFiscal cupomFiscal;
    private double vIcms;
    private double vProd;
    private double vDesc;
    private double vPis;
    private double vCofins;
    private double vPisST;
    private double vCofinsST;
    private double vOutro;
    private double vCFe;
}
