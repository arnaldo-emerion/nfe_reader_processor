package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProduto;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProdutoModel;
import br.com.arcasoftware.sbs.repository.CFeProdutoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CFeProdutoService {

    private final CFeProdutoRepository cFeProdutoRepository;

    public CFeProdutoService(CFeProdutoRepository cFeProdutoRepository) {
        this.cFeProdutoRepository = cFeProdutoRepository;
    }

    @CacheEvict(value = {"ProdutoService_getAll", "ProdutoService_getByCodigo", "ProdutoService_getByEmitenteAndCodigo", "ProdutoService_getByEmitente"}, allEntries = true)
    @Transactional
    public CFeProduto save(CFeProduto obj) {
        CFeProdutoModel model = CFeProdutoMapper.toModel(obj);
        return CFeProdutoMapper.toDomain(this.cFeProdutoRepository.save(model));
    }

    @Cacheable(value = "ProdutoService_getByCodigo")
    public Optional<CFeProduto> getByUserCreateAndCodigo(String userCreate, String codigo) {
        Optional<CFeProdutoModel> model = this.cFeProdutoRepository.getByUserCreateAndCodigo(userCreate, codigo);
        return model.map(CFeProdutoMapper::toDomain);
    }

}
