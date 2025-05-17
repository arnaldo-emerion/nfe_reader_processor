package br.com.arcasoftware.sbs.model.nfe;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Calendar;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ErroProcessamento extends BaseEntity {
    private String motivo;
    private Calendar dataProcessamento;

    public ErroProcessamento(String userCreate, String motivo){
        this.setDataProcessamento(Calendar.getInstance());
        this.setUserCreate(userCreate);
        this.setMotivo(motivo);
    }
}
