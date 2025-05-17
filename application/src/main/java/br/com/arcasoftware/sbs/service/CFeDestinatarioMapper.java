package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatario;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatarioModel;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class CFeDestinatarioMapper {

    public static CFeDestinatario toDomain(CFeDestinatarioModel model) {
        CFeDestinatario domain = new CFeDestinatario();
        BeanUtils.copyProperties(model, domain);
        return domain;
    }

    public static CFeDestinatarioModel toModel(CFeDestinatario domain) {
        if (null == domain || StringUtils.isEmpty(domain.getCpf())) return null;
        CFeDestinatarioModel model = new CFeDestinatarioModel();
        BeanUtils.copyProperties(domain, model);
        return model;
    }
}
