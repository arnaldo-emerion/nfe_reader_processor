package br.com.arcasoftware.sbs.model.cupomfiscal;

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
public class CFePisModel extends BaseEntity {
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "cfitem_id")
    private CFeItemModel cFeItem;
    private String cst;
    private double percentual;
    private double valor;
}
