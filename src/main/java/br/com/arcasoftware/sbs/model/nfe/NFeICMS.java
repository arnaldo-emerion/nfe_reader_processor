package br.com.arcasoftware.sbs.model.nfe;

import br.com.arcasoftware.sbs.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class NFeICMS extends BaseEntity {

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "nfeitem_id")
    private NFeItem nFeItem;

    private int orig;
    private String cst;
    private int modBC;
    private double vBC;
    private double pICMS;
    private double vICMS;
    private double modBCST;
    private double pMVAST;
    private double vBCST;
    private double pICMSST;
    private double vICMSST;
}
