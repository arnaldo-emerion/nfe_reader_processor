package br.com.arcasoftware.sbs.controller;

import br.com.arcasoftware.sbs.service.StatusProcessamentoNFeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("status-processamento")
@CrossOrigin("*")
public class StatusProcessamentoController {

    private final StatusProcessamentoNFeService statusProcessamentoNFeService;

    public StatusProcessamentoController(StatusProcessamentoNFeService statusProcessamentoNFeService){
        this.statusProcessamentoNFeService = statusProcessamentoNFeService;
    }

    @GetMapping("ping")
    public String ping(){
        return "ok.";
    }

    @GetMapping("processamento")
    public int getQtdNFeEmProcessamento(@RequestParam("identityId") String identityId){
        return this.statusProcessamentoNFeService.getEmProcessamento(identityId);
    }
}
