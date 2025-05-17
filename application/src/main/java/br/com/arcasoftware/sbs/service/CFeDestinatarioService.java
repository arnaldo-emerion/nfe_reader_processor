package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatario;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatarioModel;
import br.com.arcasoftware.sbs.repository.CFeDestinatarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED)
public class CFeDestinatarioService {

    private final CFeDestinatarioRepository cFeDestinatarioRepository;

    public CFeDestinatarioService(CFeDestinatarioRepository cFeDestinatarioRepository) {
        this.cFeDestinatarioRepository = cFeDestinatarioRepository;
    }

    @CacheEvict(value = {"CFeDestinatarioService_getByCnpj"}, allEntries = true)
    public CFeDestinatario save(CFeDestinatario obj) {
        CFeDestinatarioModel model = CFeDestinatarioMapper.toModel(obj);
        return CFeDestinatarioMapper.toDomain(this.cFeDestinatarioRepository.save(model));
    }

    @Cacheable(value = "CFeDestinatarioService_getByCnpj")
    public Optional<CFeDestinatario> getByUserCreateAndCpf(String userCreate, String cpf) {
        Optional<CFeDestinatarioModel> destinatarioModel = this.cFeDestinatarioRepository.getByUserCreateAndCpf(userCreate, cpf);
        return destinatarioModel.map(CFeDestinatarioMapper::toDomain);
    }

}
