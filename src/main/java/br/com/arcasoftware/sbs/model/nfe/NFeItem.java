package br.com.arcasoftware.sbs.model.nfe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
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
}
