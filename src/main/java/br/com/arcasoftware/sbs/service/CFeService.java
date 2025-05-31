package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscal;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscalModel;
import br.com.arcasoftware.sbs.repository.CFeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CFeService {

    private final CFeRepository cFeRepository;

    public CFeService(CFeRepository cFeRepository) {
        this.cFeRepository = cFeRepository;
    }

    @Transactional
    @CacheEvict(value = {"CFeService_getAll", "CFeService_getByCnpj", "CFeService_getByChaveNFe"}, allEntries = true)
    public CupomFiscal save(CupomFiscal obj) {
        CupomFiscalModel model = CupomFiscalMapper.toModel(obj);
        try {
            CupomFiscalModel save = this.cFeRepository.save(model);
            CupomFiscal domain = CupomFiscalMapper.toDomain(save);
            return domain;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    @CacheEvict(value = {"CFeService_getAll", "CFeService_getByCnpj", "CFeService_getByChaveNFe"}, allEntries = true)
    public void delete(CupomFiscalModel obj) {
        this.cFeRepository.delete(obj);
    }

    @Cacheable(value = "CFeService_getByChaveNFe")
    public List<CupomFiscalModel> findByUserCreateAndChaveNFe(String userCreate, String chaveCFe) {
        return this.cFeRepository.findByUserCreateAndChaveCFe(userCreate, chaveCFe);
    }

}
