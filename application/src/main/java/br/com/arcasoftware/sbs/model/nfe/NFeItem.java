package br.com.arcasoftware.sbs.model.nfe;

import br.com.arcasoftware.sbs.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "nfe_item_user_create_index", columnList = "userCreate"),
        @Index(name = "nfe_item_nitem", columnList = "nItem ASC")
})
public class NFeItem extends BaseEntity {

    @JsonIgnore
    @ManyToOne
    private NFe nfe;

    @OneToOne
    private Produto produto;

    private String cfop;
    private String unidade;
    private double quantidade;
    private double valorUnitario;
    private double valorTotal;
    private Integer nItem;

    @OneToOne(mappedBy = "nFeItem", cascade = CascadeType.ALL)
    private NFeICMS nFeICMS;

    @OneToOne(mappedBy = "nFeItem", cascade = CascadeType.ALL)
    private NFeIPI nFeIPI;

    @OneToOne(mappedBy = "nFeItem", cascade = CascadeType.ALL)
    private NFePIS nFePIS;

    @OneToOne(mappedBy = "nFeItem", cascade = CascadeType.ALL)
    private NFeCOFINS nFeCOFINS;
}
