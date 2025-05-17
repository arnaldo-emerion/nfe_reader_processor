package br.com.arcasoftware.sbs.model.cupomfiscal;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CFeEmitenteModel extends BaseEntity {
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String logradouro;
    private String numero;
    private String cpl;
    private String bairro;
    private String municipio;
    private String cep;
    private String ie;
    private int regimeTributario;
    private String indRatISSQN;
}
