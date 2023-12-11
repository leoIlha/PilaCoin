package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.controller.LogController;
import br.ufsm.csi.pilacoin.model.*;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.repository.TransacaoRepository;
import br.ufsm.csi.pilacoin.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.amqp.core.Message;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.query.JSqlParserUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

@Service
public class RequisicoesService {

    private PilaCoinRepository pilaCoinRepository;
    private UsuarioRepository usuarioRepository;

    private BlocoRepository blocoRepository;
    private TransacaoRepository transacaoRepository;
    private LogController logController;
    private Chaves chaves = new Chaves();
    private PublicKey pk = chaves.getPublicKey();

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public RequisicoesService(PilaCoinRepository pilaCoinRepository,UsuarioRepository usuarioRepository, BlocoRepository blocoRepository,
                              TransacaoRepository transacaoRepository,
                              LogController logController) {
        this.pilaCoinRepository=pilaCoinRepository;
        this.usuarioRepository=usuarioRepository;
        this.blocoRepository=blocoRepository;
        this.transacaoRepository = transacaoRepository;
        this.logController = logController;
    }

    public void enviarRequisicao(String routingKey, String json) {
        this.rabbitTemplate.convertAndSend(routingKey, json);
    }

    @RabbitListener(queues = {"joao_leo"})
    public void recebeMensagem(@Payload Message message) {
        String responseMessage = new String(message.getBody());
        logController.setLogsMensagens("Mensagem: " + responseMessage);
        System.out.println("Mensagem: " + responseMessage);

    }

    @SneakyThrows
    @RabbitListener(queues = {"report"})
    public void recebeReport(@Payload Message message) {
        String responseMessage = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Desserialização do JSON para um array de objetos
            Report[] arrayDeObjetos = objectMapper.readValue(responseMessage, Report[].class);

            // Processamento do array de objetos Java
            for (Report report : arrayDeObjetos) {
                String nomeUsuario = report.getNomeUsuario();
                if (nomeUsuario != null && nomeUsuario.equals("joao_leo")) {
                    System.out.println(report);
                    logController.setLogsMensagens("Report: " + report);
                }
            }
        } catch (IOException e) {
            // Lidar com exceções de desserialização
            e.printStackTrace();
        }
    }


    @RabbitListener(queues = {"joao_leo-query"})
    public void recebeMensagemquery(@Payload String strReponse) {
        //System.out.println(strReponse);
        try {
            ObjectMapper om = new ObjectMapper();
            QueryResponse queryResponse = om.readValue(strReponse, QueryResponse.class);

            //System.out.println("****************************************");
            System.out.println("idQuery: " + queryResponse.getIdQuery());
            System.out.println("usuario: " + queryResponse.getUsuario());

            if (queryResponse.getIdQuery() == 778 && queryResponse.getPilasResult() != null) {
                //System.out.println("PilasResult:");
                pilaCoinRepository.deleteAll();
                for (PilaCoinJson pila : queryResponse.getPilasResult()) {
                    // Verificar se o nonce já existe no banco antes de salvar
                    if (!pilaCoinRepository.existsByNonce(pila.getNonce())) {
                        pilaCoinRepository.save(pila);
                        //System.out.println("***********************");

                    }
                }
            } else if (queryResponse.getIdQuery() == 777 && queryResponse.getUsuariosResult() != null) {
                //System.out.println("UsuariosResult:");
                usuarioRepository.deleteAll();
                for (Usuario u : queryResponse.getUsuariosResult()) {
                    try {
                        // Verificar se o usuário já existe no banco antes de salvar
                        if (!usuarioRepository.existsByNome(u.getNome())) {
                            // Salvar apenas se não existir
                            usuarioRepository.save(u);
                        } else {
                            //System.out.println("Usuario já está no banco " + u.getNome() + " Não será salvo novamente.");
                        }
                    } catch (DataIntegrityViolationException e) {
                        // Capturar exceção de chave duplicada
                        //System.out.println("Erro ao salvar usuário " + u.getNome() + ": Chave duplicada. Usuário já existe no banco.");
                    }
                }
            } else if (queryResponse.getIdQuery() == 779 && queryResponse.getBlocosResult() != null) {
                //System.out.println("BlocosResult:");
                blocoRepository.deleteAll();
                for (Bloco b : queryResponse.getBlocosResult()) {
                   // System.out.println(b.getNomeUsuarioMinerador());
                    try {
                            if(!blocoRepository.existsByNonce(b.getNonce())) {
                                //System.out.println("***********************");
                                //System.out.println("BLOCO+" +b.getNonce()+ "SERÁ SALVO NO BANCO");
                                blocoRepository.save(b);
                            }else {
                                //System.out.println("Bloco " + b.getNonce() + " Não será salvo novamente.");
                            }
                    } catch (DataIntegrityViolationException e) {
                        // Capturar exceção de chave duplicada
                        //System.out.println("Já esta no bano");
                    }
                }
            } else if (queryResponse.getIdQuery() == 800 && queryResponse.getPilasResult() != null) {
                System.out.println("Foram encontrados: "+queryResponse.getPilasResult().size() + " pilas");
                byte[] encoded = pk.getEncoded();
                String stringChave = Base64.getEncoder().encodeToString(encoded);

                for (PilaCoinJson pila : queryResponse.getPilasResult()) {
                    Transacao ultimaTransacao = pila.getTransacoes().get(pila.getTransacoes().size() - 1);
                    if (ultimaTransacao.getChaveUsuarioDestino() != null) {
                        if (ultimaTransacao.getChaveUsuarioDestino().equals(stringChave)) {
                            pilaCoinRepository.save(pila);
                        }
                    }
                }
            }

        } catch (IOException e) {

        }
    }



}




