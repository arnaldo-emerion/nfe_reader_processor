package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.Emitente;
import br.com.arcasoftware.sbs.repository.EmitenteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
public class EmitenteService {

    private final EmitenteRepository emitenteRepository;

    public EmitenteService(EmitenteRepository emitenteRepository) {
        this.emitenteRepository = emitenteRepository;
    }

    @CacheEvict(value = {"EmitenteService_getByCnpj"}, allEntries = true)
    public Emitente save(Emitente obj) {
        return this.emitenteRepository.save(obj);
    }

    @Cacheable(value = "EmitenteService_getByCnpj")
    public Optional<Emitente> getByUserCreateAndCnpj(String userCreate, String cnpj) {
        return this.emitenteRepository.getByUserCreateAndCnpj(userCreate, cnpj);
    }

}
