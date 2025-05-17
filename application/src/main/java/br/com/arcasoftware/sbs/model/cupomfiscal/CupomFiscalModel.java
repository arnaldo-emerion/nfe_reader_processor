package br.com.arcasoftware.sbs.model.cupomfiscal;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class CupomFiscalModel extends BaseEntity {
    private String uf;
    private String nf;
    private String numCupomFiscal;
    private Instant dataEmissao;
    private String cnpj;
    @Column(nullable = false)
    private String chaveCFe;
    private String natOperacao;

    @ManyToOne
    private CFeEmitenteModel emitente;

    @ManyToOne
    private CFeDestinatarioModel destinatario;

    @OneToMany(mappedBy = "cupomFiscal", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CFeItemModel> cFeItemList;

    @OneToOne(mappedBy = "cupomFiscal", cascade = CascadeType.ALL)
    private CFeIcmsTotalModel cFeIcmsTotal;
}
