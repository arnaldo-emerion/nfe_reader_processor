package br.com.arcasoftware.sbs.model.nfe;

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
public class ErroProcessamento extends BaseEntity {
    private String motivo;

    public ErroProcessamento(String userCreate, String motivo){
        this.setUserCreate(userCreate);
        this.setMotivo(motivo);
    }
}
