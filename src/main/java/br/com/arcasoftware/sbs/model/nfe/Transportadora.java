package br.com.arcasoftware.sbs.model.nfe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Transportadora extends BaseEntity {

    @Column(unique = true)
    private String cnpj;
    private String razaoSocial;
    private String ie;
    private String uf;
    private String municipio;
    private String endereco;
}
