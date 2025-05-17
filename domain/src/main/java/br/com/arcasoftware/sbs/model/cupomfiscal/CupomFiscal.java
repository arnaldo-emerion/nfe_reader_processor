package br.com.arcasoftware.sbs.model.cupomfiscal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CupomFiscal {
    private Long id;
    private String userCreate;
    private String uf;
    private String nf;
    private String numCupomFiscal;
    private Instant dataEmissao;
    private String cnpj;
    private String chaveCFe;
    private CFeEmitente emitente;
    private CFeDestinatario destinatario;
    private List<CFeItem> cFeItemList;
    private CFeIcmsTotal cFeIcmsTotal;
    private String natOperacao;
}
