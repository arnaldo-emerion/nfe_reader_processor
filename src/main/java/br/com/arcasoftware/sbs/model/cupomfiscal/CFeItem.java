package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFeItem {
    private Long id;
    private String userCreate;
    private CupomFiscal cupomFiscal;
    private CFeProduto cFeProduto;
    private String cfop;
    private String unidade;
    private double quantidade;
    private double valorUnitario;
    private double valorProduto;
    private String indRegra;
    private double desconto;
    private double valorOutros;
    private double valorTotalItem;
    private CFeIcms cFeIcms;
    private CFePis cFePis;
    private CFeCofins cFeCofins;
}
