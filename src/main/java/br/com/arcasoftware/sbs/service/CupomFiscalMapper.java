package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItemModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProdutoModel;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscal;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscalModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;

@Slf4j
public class CupomFiscalMapper {

    public static CupomFiscal toDomain(CupomFiscalModel model) {
        CupomFiscal cupomFiscal = new CupomFiscal();
        BeanUtils.copyProperties(model, cupomFiscal);
        return cupomFiscal;
    }

    public static CupomFiscalModel toModel(CupomFiscal domain) {
        CupomFiscalModel model = new CupomFiscalModel();
        model.setUf(domain.getUf());
        model.setNf(domain.getNf());
        model.setNumCupomFiscal(domain.getNumCupomFiscal());
        model.setDataEmissao(domain.getDataEmissao());
        model.setCnpj(domain.getCnpj());
        model.setChaveCFe(domain.getChaveCFe());
        model.setEmitente(CFeEmitenteMapper.toModel(domain.getEmitente()));
        model.setDestinatario(CFeDestinatarioMapper.toModel(domain.getDestinatario()));
        model.setCFeIcmsTotal(CFeIcmsTotalMapper.toModel(domain.getCFeIcmsTotal(), model));
        model.setCFeItemList(new ArrayList<>());
        model.setNatOperacao(domain.getNatOperacao());
        domain.getCFeItemList().forEach(item -> {
                    CFeProdutoModel cFeProdutoModel = new CFeProdutoModel();
                    cFeProdutoModel.setId(item.getCFeProduto().getId());
                    CFeItemModel itemModel = CFeItemMapper.toModel(item, cFeProdutoModel);
                    itemModel.setCupomFiscal(model);
                    itemModel.setCFePis(CFePisMapper.toModel(item.getCFePis(), itemModel));
                    itemModel.setCFeCofins(CFeCofinsMapper.toModel(item.getCFeCofins(), itemModel));
                    itemModel.setCFeIcms(CFeIcmsMapper.toModel(item.getCFeIcms(), itemModel));
                    model.getCFeItemList().add(itemModel);
                }
        );
        model.setUserCreate(domain.getUserCreate());
        return model;
    }

}
