package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.*;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import br.ufsm.csi.pilacoin.service.RequisicoesService;
import br.ufsm.csi.pilacoin.utils.Funcoes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/carteira")
@CrossOrigin
public class TransferenciaController {

    private RequisicoesService requisicoesService;
    private PilaCoinRepository pilaCoinRepository;
    private UsuarioRepository usuarioRepository;
    private BlocoRepository blocoRepository;
    private LogController logController;

    public TransferenciaController(RequisicoesService requisicoesService, PilaCoinRepository pilaCoinRepository, UsuarioRepository usuarioRepository,
                                   BlocoRepository blocoRepository,
                                   LogController logController) {
        this.requisicoesService = requisicoesService;
        this.pilaCoinRepository = pilaCoinRepository;
        this.usuarioRepository = usuarioRepository;
        this.blocoRepository = blocoRepository;
        this.logController = logController;
    }

    @GetMapping("/usuarios")
    public ArrayList<Usuario> getUsuarios() {
        return (ArrayList<Usuario>) this.usuarioRepository.findAll();
    }

    @GetMapping("/pilas")
    public ArrayList<PilaCoinJson> getPilas() {
        return (ArrayList<PilaCoinJson>) this.pilaCoinRepository.findAll();
    }

    @GetMapping("/blocos")
    public ArrayList<Bloco> getBlocos() {
        return (ArrayList<Bloco>) this.blocoRepository.findAll();
    }

    @SneakyThrows
    @PostMapping("/transferir")
    public void transferirPila(@RequestBody TransferenciaPila transferenciaPila) {
        transferenciaPila.setDataTransacao(new Date(System.currentTimeMillis()));
        transferenciaPila.setNomeUsuarioOrigem("joao_leo");
        Chaves chaves = new Chaves();
        transferenciaPila.setChaveUsuarioOrigem(chaves.getPublicKey().getEncoded());

        ObjectMapper om = new ObjectMapper();

        String json = om.writeValueAsString(transferenciaPila);

        Funcoes funcoes = new Funcoes();

        transferenciaPila.setAssinatura(funcoes.geraAssinatura(json));

        System.out.println("Tranferindo o pila...");

        System.out.println(om.writeValueAsString(transferenciaPila));
        requisicoesService.enviarRequisicao("transferir-pila", om.writeValueAsString(transferenciaPila));
        logController.setLogsMensagens("Tranferindo o pila...\n" + om.writeValueAsString(transferenciaPila));
        this.pilaCoinRepository.deleteByNonce(transferenciaPila.getNoncePila());
    }
}
