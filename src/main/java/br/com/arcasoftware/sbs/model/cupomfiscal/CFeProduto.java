package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CFeProduto {
    private Long id;
    private String userCreate;
    private String codigo;
    private String descricao;
    private String ncm;
    private String cnpjEmitente;
}
