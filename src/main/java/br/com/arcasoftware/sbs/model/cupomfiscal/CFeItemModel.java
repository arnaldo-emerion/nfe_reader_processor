package br.com.arcasoftware.sbs.model.cupomfiscal;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CFeItemModel extends BaseEntity {
    @ManyToOne
    private CupomFiscalModel cupomFiscal;
    @ManyToOne
    private CFeProdutoModel cFeProduto;
    private String cfop;
    private String unidade;
    private double quantidade;
    private double valorUnitario;
    private double valorProduto;
    private String indRegra;
    private double desconto;
    private double valorOutros;
    private double valorTotalItem;

    @OneToOne(mappedBy = "cFeItem", cascade = CascadeType.ALL)
    private CFeIcmsModel cFeIcms;

    @OneToOne(mappedBy = "cFeItem", cascade = CascadeType.ALL)
    private CFePisModel cFePis;

    @OneToOne(mappedBy = "cFeItem", cascade = CascadeType.ALL)
    private CFeCofinsModel cFeCofins;

}
