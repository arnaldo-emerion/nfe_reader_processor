package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import br.com.arcasoftware.sbs.repository.ErroProcessamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ErroProcessamentoService {

    private final ErroProcessamentoRepository erroProcessamentoRepository;

    public ErroProcessamentoService(ErroProcessamentoRepository erroProcessamentoRepository) {
        this.erroProcessamentoRepository = erroProcessamentoRepository;
    }

    @Transactional
    public ErroProcessamento save(ErroProcessamento obj) {
        return this.erroProcessamentoRepository.save(obj);
    }


}
