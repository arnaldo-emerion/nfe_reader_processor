package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitente;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitenteModel;
import br.com.arcasoftware.sbs.repository.CFeEmitenteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
public class CFeEmitenteService {

    private final CFeEmitenteRepository cFeEmitenteRepository;

    public CFeEmitenteService(CFeEmitenteRepository cFeEmitenteRepository) {
        this.cFeEmitenteRepository = cFeEmitenteRepository;
    }

    @CacheEvict(value = {"CFeEmitenteService_getByCnpj"}, allEntries = true)
    public CFeEmitente save(CFeEmitente obj) {
        CFeEmitenteModel model = CFeEmitenteMapper.toModel(obj);
        return CFeEmitenteMapper.toDomain(this.cFeEmitenteRepository.save(model));
    }

    @Cacheable(value = "CFeEmitenteService_getByCnpj")
    public Optional<CFeEmitente> getByUserCreateAndCnpj(String userCreate, String cnpj) {
        Optional<CFeEmitenteModel> cFeEmitenteModel = this.cFeEmitenteRepository.getFirstByUserCreateAndCnpj(userCreate, cnpj);
        return cFeEmitenteModel.map(CFeEmitenteMapper::toDomain);
    }

}
