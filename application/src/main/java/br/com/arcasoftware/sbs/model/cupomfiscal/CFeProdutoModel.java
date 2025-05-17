package br.com.arcasoftware.sbs.model.cupomfiscal;

import br.com.arcasoftware.sbs.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo", "userCreate"})})
public class CFeProdutoModel extends BaseEntity {

    private String codigo;
    private String descricao;
    private String ncm;
    private String cnpjEmitente;
}
