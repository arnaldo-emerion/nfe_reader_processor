package br.com.arcasoftware.sbs.model.nfe;

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
public class NFeTotalICMS extends BaseEntity {

    @JsonIgnore
    @OneToOne
    private NFe nfe;

    private double vBC;
    private double vICMS;
    private double vBCST;
    private double vST;
    private double vProd;
    private double vFrete;
    private double vSeg;
    private double vDesc;
    private double vII;
    private double vIPI;
    private double vPIS;
    private double vCOFINS;
    private double vOutro;
    private double vNF;
    private double vICMSDeson;
    private double vFCP;
    private double vICMSUFDest;
    private double vICMSUFRemet;
    private double vFCPSTRet;
    private double pFCPSTRet;
    private double vIPIDevol;
    private double vTotTrib;
}
