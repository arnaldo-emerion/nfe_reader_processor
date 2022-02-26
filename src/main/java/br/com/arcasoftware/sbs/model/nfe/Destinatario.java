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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj", "userCreate" }) })
public class Destinatario extends BaseEntity implements FiltravelPorEmitente {

    private String cnpj;
    private String razaoSocial;
    private String ie;
    private String uf;
    private String municipio;
    private String bairro;
    private String telefone;
    private String cep;
    private String logradouro;
    private String numero;
    private String cPais;
    private String xPais;
    private String indIEDest;

    @JsonIgnore
    @ManyToOne
    private Emitente emitente;

    @JsonIgnore
    @Override
    public String getEmitenteCNPJ() {
        return this.getEmitente().getCnpj();
    }
}
