package br.com.arcasoftware.sbs.model.nfe;


import br.com.arcasoftware.sbs.model.BaseEntity;
import br.com.arcasoftware.sbs.model.FiltravelPorEmitente;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(indexes = {
        @Index(name = "nfe_user_create_index", columnList = "userCreate"),
        @Index(name = "nfe_destinatario_index", columnList = "destinatario_id"),
        @Index(name = "nfe_data_emissao_index", columnList = "dataEmissao"),
        @Index(name = "nfe_natureza_operacao_index", columnList = "natOperacao"),
},
        uniqueConstraints = { @UniqueConstraint(columnNames = { "chaveNFe", "userCreate" }) })
public class NFe extends BaseEntity implements FiltravelPorEmitente {

    private String cUF;
    private String cNF;
    private String natOperacao;
    private String nNF;

    @Temporal(TemporalType.DATE)
    private Date dataEmissao;
    private String tpNf;
    private String cMunFG;

    @Column(nullable = false)
    private String chaveNFe;

    @Column(length = 1024)
    private String infoAdicional;

    @OneToOne
    private Emitente emitente;

    @OneToOne
    private Destinatario destinatario;

    @OneToOne
    private Transportadora transportadora;

    @OneToMany(mappedBy = "nfe", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<NFeItem> nFeItemList;

    @OneToOne(mappedBy = "nfe", cascade = CascadeType.ALL)
    private NFeTotalICMS nFeTotalICMS;

    @JsonIgnore
    @Override
    public String getEmitenteCNPJ() {
        return this.getEmitente().getCnpj();
    }

    private String fileName;
}
