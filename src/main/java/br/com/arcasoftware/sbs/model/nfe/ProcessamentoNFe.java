package br.com.arcasoftware.sbs.model.nfe;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "processamentonfe")
public class ProcessamentoNFe extends BaseEntity {
    @Column(name = "file_name")
    private String fileName;
    private String status;
    private Calendar dataRecebimento;
    @Column(name = "data_finalizacao")
    private Calendar dataFinalizacao;

    public ProcessamentoNFe() {
        this.setDataRecebimento(Calendar.getInstance());
    }
}
