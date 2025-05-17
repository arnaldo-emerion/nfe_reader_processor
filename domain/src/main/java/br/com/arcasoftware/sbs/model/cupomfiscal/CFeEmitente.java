package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFeEmitente {
    private Long id;
    private String userCreate;
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
