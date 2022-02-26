package br.com.arcasoftware.sbs.model.nfe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "cnpj", "userCreate" }) })
public class Emitente extends BaseEntity {

    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String ie;
    private String crt;
    private String uf;
    private String municipio;
    private String bairro;
    private String telefone;
    private String logradouro;
    private String cPais;
    private String xPais;
    private String cep;
}
