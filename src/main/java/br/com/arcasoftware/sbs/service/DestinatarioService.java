package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.Destinatario;
import br.com.arcasoftware.sbs.model.nfe.Emitente;
import br.com.arcasoftware.sbs.repository.DestinatarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DestinatarioService {

    private final DestinatarioRepository destRepository;

    public DestinatarioService(DestinatarioRepository destRepository) {
        this.destRepository = destRepository;
    }

    @Cacheable(value = "DestService_getByCnpj")
    public Optional<Destinatario> getByUserCreateAndCnpj(String userCreate, String cnpj) {
        return this.destRepository.getByUserCreateAndCnpj(userCreate, cnpj);
    }

    @Cacheable(value = "DestService_getByEmitenteAndCnpj")
    public Optional<Destinatario> getByUserCreateAndEmitenteAndCnpj(String userCreate, Emitente emitente, String cnpj) {
        return this.destRepository.getByUserCreateAndEmitenteAndCnpj(userCreate, emitente, cnpj);
    }

    @CacheEvict(value = {"DestService_getByCnpj", "DestService_getByEmitenteAndCnpj"}, allEntries = true)
    public Destinatario save(Destinatario obj) {
        return this.destRepository.saveAndFlush(obj);
    }


}
