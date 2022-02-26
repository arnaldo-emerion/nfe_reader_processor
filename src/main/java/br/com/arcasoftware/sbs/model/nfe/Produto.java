package br.com.arcasoftware.sbs.model.nfe;

import br.com.arcasoftware.sbs.model.FiltravelPorEmitente;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "codigo", "userCreate" }) })
public class Produto extends BaseEntity implements FiltravelPorEmitente {

    private String codigo;
    private String ean;
    private String descricao;
    private String ncm;
    private String unidade;

    @JsonIgnore
    @ManyToOne
    private Emitente emitente;

    @JsonIgnore
    @Override
    public String getEmitenteCNPJ() {
        return this.getEmitente().getCnpj();
    }
}
