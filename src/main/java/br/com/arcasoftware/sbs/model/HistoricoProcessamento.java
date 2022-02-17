package br.com.arcasoftware.sbs.model;

import br.com.arcasoftware.sbs.model.nfe.BaseEntity;
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
public class HistoricoProcessamento extends BaseEntity {
    private String nomeArquivo;
    private Calendar dataProcessamento;
    private boolean processadaCorretamente;
    private String motivo;
}
