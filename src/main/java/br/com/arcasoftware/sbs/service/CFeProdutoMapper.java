package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitenteModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProduto;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProdutoModel;
import org.springframework.beans.BeanUtils;

public class CFeProdutoMapper {

    public static CFeProduto toDomain(CFeProdutoModel model) {
        CFeProduto domain = new CFeProduto();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeProdutoModel toModel(CFeProduto domain) {
        CFeProdutoModel model = new CFeProdutoModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
