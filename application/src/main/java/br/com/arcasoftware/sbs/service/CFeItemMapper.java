package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItem;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItemModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProduto;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProdutoModel;
import org.springframework.beans.BeanUtils;

public class CFeItemMapper {

    public static CFeItem toDomain(CFeItemModel model) {
        CFeItem domain = new CFeItem();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeItemModel toModel(CFeItem domain, CFeProdutoModel produtoModel) {
        CFeItemModel model = new CFeItemModel();
        BeanUtils.copyProperties(domain, model);
        model.setCFeProduto(produtoModel);
        return model;
    }
}
