package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.Emitente;
import br.com.arcasoftware.sbs.model.nfe.Produto;
import br.com.arcasoftware.sbs.repository.ProdutoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @CacheEvict(value = {"ProdutoService_getAll", "ProdutoService_getByCodigo", "ProdutoService_getByEmitenteAndCodigo", "ProdutoService_getByEmitente"}, allEntries = true)
    @Transactional
    public Produto save(Produto obj) {
        return this.produtoRepository.save(obj);
    }

    @Cacheable(value = "ProdutoService_getByCodigo")
    public Optional<Produto> getByUserCreateAndCodigo(String userCreate, String codigo) {
        return this.produtoRepository.getByUserCreateAndCodigo(userCreate, codigo);
    }


    @Cacheable(value = "ProdutoService_getByEmitente")
    public List<Produto> getByUserCreateAndEmitente(String userCreate, Emitente emitente) {
        return this.produtoRepository.getByUserCreateAndEmitente(userCreate, emitente);
    }


}
