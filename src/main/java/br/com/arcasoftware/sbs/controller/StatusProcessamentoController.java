package br.com.arcasoftware.sbs.controller;

import br.com.arcasoftware.sbs.model.nfe.ProcessamentoNFe;
import br.com.arcasoftware.sbs.service.ProcessamentoNFeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("status-processamento")
@CrossOrigin("*")
public class StatusProcessamentoController {

    private final ProcessamentoNFeService processamentoNFeService;

    public StatusProcessamentoController(ProcessamentoNFeService processamentoNFeService) {
        this.processamentoNFeService = processamentoNFeService;
    }

    @GetMapping("ping")
    public String ping() {
        return "ok.";
    }

    @GetMapping("processamento")
    public int getQtdNFeEmProcessamento(@RequestParam("identityId") String identityId) {
        return this.processamentoNFeService.getEmProcessamento(identityId);
    }

    @PostMapping("add/{identityId}/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public void addStatusProcessamento(@PathVariable("identityId") String identityId, @PathVariable("fileName") String fileName) {
        ProcessamentoNFe processamentoNFe = new ProcessamentoNFe();
        processamentoNFe.setFileName(fileName);
        processamentoNFe.setUserCreate(identityId);
        processamentoNFe.setStatus("RECEBIDO");
        this.processamentoNFeService.save(processamentoNFe);
    }
}
