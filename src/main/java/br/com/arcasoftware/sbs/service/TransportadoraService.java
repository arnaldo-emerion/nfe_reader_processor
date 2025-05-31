package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.Transportadora;
import br.com.arcasoftware.sbs.repository.TransportadoraRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransportadoraService {

    private final TransportadoraRepository transportadoraRepository;

    public TransportadoraService(TransportadoraRepository transportadoraRepository) {
        this.transportadoraRepository = transportadoraRepository;
    }

    @Transactional
    @CacheEvict(value = {"TransportadoraService_getByCnpj"}, allEntries = true)
    public Transportadora save(Transportadora obj) {
        return this.transportadoraRepository.save(obj);
    }

    @Cacheable(value = "TransportadoraService_getByCnpj")
    public Optional<Transportadora> getByUserCreateAndCnpj(String userCreate, String cnpj) {
        return this.transportadoraRepository.getByUserCreateAndCnpj(userCreate, cnpj);
    }
}
