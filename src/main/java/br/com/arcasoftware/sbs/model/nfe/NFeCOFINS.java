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
public class NFeCOFINS extends BaseEntity {

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "nfeitem_id")
    private NFeItem nFeItem;

    private String cst;
    private double vBC;
    private double pCOFINS;
    private double vCOFINS;
}
