package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.controller.TransferenciaController;
import br.ufsm.csi.pilacoin.model.*;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PegaTudoService {

    private RequisicoesService requisicoesService;
    private PilaCoinRepository pilaCoinRepository;

    private ArrayList<Usuario> usuarios;

    public PegaTudoService(RequisicoesService requisicoesService, PilaCoinRepository pilaCoinRepository) {
        this.requisicoesService = requisicoesService;
        this.pilaCoinRepository = pilaCoinRepository;
    }

    public void atualizar() {
        pegaUsuario();
        pegaPilasValidos();
        pegaBlocosValidos();
        pegaPilas();
    }

    @SneakyThrows
    public void pegaUsuario() {
        Query query = Query.builder().
                idQuery(777).
                nomeUsuario("joao_leo").
                tipoQuery(Query.tiposQuery.USUARIOS).
                build();
        ObjectMapper om = new ObjectMapper();

        requisicoesService.enviarRequisicao("query", om.writeValueAsString(query));
    }

    @SneakyThrows
    public void pegaPilasValidos() {
        Query query = Query.builder().
                idQuery(778).
                nomeUsuario("joao_leo").
                tipoQuery(Query.tiposQuery.PILA).
                usuarioMinerador("joao_leo").
                status("VALIDO").
                build();
        ObjectMapper om = new ObjectMapper();

        requisicoesService.enviarRequisicao("query", om.writeValueAsString(query));
    }

    @SneakyThrows
    public void pegaBlocosValidos() {
        Query query = Query.builder().
                idQuery(779).
                nomeUsuario("joao_leo").
                tipoQuery(Query.tiposQuery.BLOCO).
                usuarioMinerador("joao_leo").
                status("VALIDO").
                build();
        ObjectMapper om = new ObjectMapper();

        requisicoesService.enviarRequisicao("query", om.writeValueAsString(query));
    }

    @SneakyThrows
    public void pegaPilas() {
        Query query = Query.builder().
                idQuery(800).
                nomeUsuario("joao_leo").
                tipoQuery(Query.tiposQuery.PILA).
                status("VALIDO").
                build();
        ObjectMapper om = new ObjectMapper();

        requisicoesService.enviarRequisicao("query", om.writeValueAsString(query));
    }
}
