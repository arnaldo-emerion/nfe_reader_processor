package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.HistoricoProcessamento;
import br.com.arcasoftware.sbs.repository.HistoricoProcessamentoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricoProcessamentoService {

    private final HistoricoProcessamentoRepository repository;

    public HistoricoProcessamentoService(HistoricoProcessamentoRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "HistoricoProcessamentoService_getAll", allEntries = true)
    @Transactional
    public HistoricoProcessamento save(HistoricoProcessamento obj) {
        return this.repository.save(obj);
    }

}
