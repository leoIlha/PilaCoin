package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.service.PegaTudoService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/dados")
@CrossOrigin
public class DadosController {

    private PegaTudoService pegaTudoService;

    public DadosController(PegaTudoService pegaTudoService) {
        this.pegaTudoService = pegaTudoService;
    }

    @GetMapping("/atualizar")
    public int atualizar() {
        pegaTudoService.atualizar();
        return 1;
    }
}
