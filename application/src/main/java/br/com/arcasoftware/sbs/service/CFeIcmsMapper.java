package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcms;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcmsModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItemModel;
import org.springframework.beans.BeanUtils;

public class CFeIcmsMapper {

    public static CFeIcms toDomain(CFeIcmsModel model) {
        CFeIcms domain = new CFeIcms();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeIcmsModel toModel(CFeIcms domain, CFeItemModel itemModel) {
        CFeIcmsModel model = new CFeIcmsModel();
        BeanUtils.copyProperties(domain, model);
        model.setCFeItem(itemModel);
        return model;
    }
}
