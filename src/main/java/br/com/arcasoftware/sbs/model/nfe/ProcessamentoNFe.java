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
public class ProcessamentoNFe extends BaseEntity {
    @Column(name = "file_name")
    private String fileName;
    private String status;
}
