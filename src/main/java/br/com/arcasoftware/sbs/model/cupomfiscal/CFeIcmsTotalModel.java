package br.com.arcasoftware.sbs.model.cupomfiscal;

import br.com.arcasoftware.sbs.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CFeIcmsTotalModel extends BaseEntity {
    @JsonIgnore
    @OneToOne
    private CupomFiscalModel cupomFiscal;

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
