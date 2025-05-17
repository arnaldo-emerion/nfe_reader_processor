package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeCofins;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeCofinsModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItemModel;
import org.springframework.beans.BeanUtils;

public class CFeCofinsMapper {

    public static CFeCofins toDomain(CFeCofinsModel model) {
        CFeCofins domain = new CFeCofins();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeCofinsModel toModel(CFeCofins domain, CFeItemModel itemModel) {
        CFeCofinsModel model = new CFeCofinsModel();
        BeanUtils.copyProperties(domain, model);
        model.setCFeItem(itemModel);
        return model;
    }
}
