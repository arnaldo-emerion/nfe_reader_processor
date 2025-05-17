package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitente;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitenteModel;
import org.springframework.beans.BeanUtils;

public class CFeEmitenteMapper {

    public static CFeEmitente toDomain(CFeEmitenteModel model) {
        CFeEmitente domain = new CFeEmitente();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeEmitenteModel toModel(CFeEmitente domain) {
        CFeEmitenteModel model = new CFeEmitenteModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
