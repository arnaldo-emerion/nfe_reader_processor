package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcmsTotal;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcmsTotalModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscalModel;
import org.springframework.beans.BeanUtils;

public class CFeIcmsTotalMapper {

    public static CFeIcmsTotal toDomain(CFeIcmsTotalModel model) {
        CFeIcmsTotal cFeIcmsTotal = new CFeIcmsTotal();
        BeanUtils.copyProperties(model, cFeIcmsTotal);
        return cFeIcmsTotal;
    }

    public static CFeIcmsTotalModel toModel(CFeIcmsTotal domain, CupomFiscalModel cupomFiscalModel) {
        CFeIcmsTotalModel model = new CFeIcmsTotalModel();
        BeanUtils.copyProperties(domain, model);
        model.setCupomFiscal(cupomFiscalModel);
        return model;
    }

}
