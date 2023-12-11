package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.controller.LogController;
import br.ufsm.csi.pilacoin.model.Bloco;
import br.ufsm.csi.pilacoin.model.Chaves;
import br.ufsm.csi.pilacoin.model.ValidacaoBlocoJson;
import br.ufsm.csi.pilacoin.repository.BlocoRepository;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.utils.Funcoes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

@Service
public class ValidaBlocoService {

    private final RequisicoesService requisicoesService;
    private DificuldadeService dificuldadeService;
    private static ArrayList<String> ignoreBlocos = new ArrayList<>();
    private LogController logController;

    private BlocoRepository blocoRepository;

    public ValidaBlocoService(RequisicoesService requisicoesService, DificuldadeService dificuldadeService, BlocoRepository blocoRepository, LogController logController) {
        this.requisicoesService = requisicoesService;
        this.dificuldadeService = dificuldadeService;
        this.blocoRepository=blocoRepository;
        this.logController = logController;
    }

    @SneakyThrows
    @RabbitListener(queues = {"bloco-minerado"})
    public void getMinerados(@Payload String strBloco) {

        //System.out.println(strBloco);

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                boolean blocoExistente = true;

//                for (String bloco : ignoreBlocos) {
//                    if (bloco.equals(strBloco)) {
//                        blocoExistente = true;
//                        break;
//                    }
//                }

                if (blocoExistente) {
                    ignoreBlocos.add(strBloco);

                    ObjectMapper ob = new ObjectMapper();
                    Bloco bloco = ob.readValue(strBloco, Bloco.class);

                    try {
                        if (bloco.getNomeUsuarioMinerador().equals("joao_leo")) {
                            requisicoesService.enviarRequisicao("bloco-minerado", strBloco);
                            //blocoRepository.save(bloco);
                        } else {
                            MessageDigest md = MessageDigest.getInstance("SHA-256");
                            BigInteger hash = new BigInteger(md.digest(strBloco.getBytes(StandardCharsets.UTF_8))).abs(); //Ver se não falta isso nas outras hashs

                            synchronized (dificuldadeService) {
                                if (dificuldadeService.getDif() == null) {
                                    dificuldadeService.wait();
                                }
                            }

                            if (!(hash.compareTo(dificuldadeService.getDif()) > 0)) {
                                System.out.println("Validando bloco do(a): " + bloco.getNomeUsuarioMinerador());
                                logController.setLogsValidacaoBloco("Validando bloco do(a): " + bloco.getNomeUsuarioMinerador());

                                md.reset();

                                Funcoes funcoes = new Funcoes();

                                Chaves chaves = new Chaves();

                                ValidacaoBlocoJson validacaoBlocoJson = ValidacaoBlocoJson.builder().
                                        assinaturaBloco(funcoes.geraAssinatura(strBloco)).
                                        //chavePublicaValidador(publicKey.toString().getBytes(StandardCharsets.UTF_8)).
                                        chavePublicaValidador(chaves.getPublicKey().getEncoded()).
                                        nomeValidador("joao_leo").
                                        bloco(bloco).
                                        build();

                                String jsonValidado = ob.writeValueAsString(validacaoBlocoJson);

                                requisicoesService.enviarRequisicao("bloco-validado", jsonValidado);
                                logController.setLogsValidacaoBloco("Bloco validado \nMinha assinatura: " + jsonValidado);
                                System.out.println("Validado!");
                            } else {
                                System.out.println("String do bloco não atingiu a hash");
                                requisicoesService.enviarRequisicao("bloco-minerado", strBloco);
                                logController.setLogsValidacaoBloco("String do bloco de nonce: "+bloco.getNonce()+" não atingiu a hash");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Houve algum erro ao validar o bloco!");
                        requisicoesService.enviarRequisicao("bloco-minerado", strBloco);
                    }

                }
            }
        }).start();
    }
}
