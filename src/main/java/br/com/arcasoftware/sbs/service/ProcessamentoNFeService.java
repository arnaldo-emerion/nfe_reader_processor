package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.model.nfe.ProcessamentoNFe;
import br.com.arcasoftware.sbs.repository.ProcessamentoNFeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessamentoNFeService {

    private final ProcessamentoNFeRepository processamentoNFeRepository;

    public ProcessamentoNFeService(ProcessamentoNFeRepository processamentoNFeRepository) {
        this.processamentoNFeRepository = processamentoNFeRepository;
    }

    @Transactional
    public ProcessamentoNFe save(ProcessamentoNFe obj) {
        return this.processamentoNFeRepository.save(obj);
    }

    @Transactional
    public void finalizeProcessamento(String identityId, String fileName) {
        this.processamentoNFeRepository.finalizeProcessamento(identityId, fileName);
    }

    public int getEmProcessamento(String identityId) {
        return this.processamentoNFeRepository.getEmProcessamento(identityId);
    }
}
