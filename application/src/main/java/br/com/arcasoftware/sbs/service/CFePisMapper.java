package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItemModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFePis;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFePisModel;
import org.springframework.beans.BeanUtils;

public class CFePisMapper {

    public static CFePis toDomain(CFePisModel model) {
        CFePis domain = new CFePis();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFePisModel toModel(CFePis domain, CFeItemModel itemModel) {
        CFePisModel model = new CFePisModel();
        BeanUtils.copyProperties(domain, model);
        model.setCFeItem(itemModel);
        return model;
    }
}
