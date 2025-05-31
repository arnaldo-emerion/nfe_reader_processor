package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.NFe;
import br.com.arcasoftware.sbs.repository.NFeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NFeService {

    private final NFeRepository nFeRepository;

    public NFeService(NFeRepository nFeRepository) {
        this.nFeRepository = nFeRepository;
    }

    @Transactional
    @CacheEvict(value = {"NFeService_getAll", "NFeService_getByCnpj", "NFeService_getByChaveNFe"}, allEntries = true)
    public NFe save(NFe obj) {
        return this.nFeRepository.save(obj);
    }

    @Transactional
    @CacheEvict(value = {"NFeService_getAll", "NFeService_getByCnpj", "NFeService_getByChaveNFe"}, allEntries = true)
    public void delete(NFe obj) {
        this.nFeRepository.delete(obj);
    }

    @Cacheable(value = "NFeService_getByChaveNFe")
    public List<NFe> findByUserCreateAndChaveNFe(String userCreate, String chaveNFe) {
        return this.nFeRepository.findByUserCreateAndChaveNFe(userCreate, chaveNFe);
    }

}
