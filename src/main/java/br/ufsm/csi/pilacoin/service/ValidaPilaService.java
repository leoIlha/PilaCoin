package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.controller.LogController;
import br.ufsm.csi.pilacoin.model.Chaves;
import br.ufsm.csi.pilacoin.model.PilaCoin;
import br.ufsm.csi.pilacoin.model.PilaCoinJson;
import br.ufsm.csi.pilacoin.model.ValidacaoPilaJson;
import br.ufsm.csi.pilacoin.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.utils.Funcoes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.coyote.http11.filters.ChunkedInputFilter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

@Service
public class ValidaPilaService {
    private final RequisicoesService requisicoesService;
    private DificuldadeService dificuldadeService;
    private static ArrayList<String> ignorePilas = new ArrayList<>();
    private LogController logController;

    private PilaCoinRepository pilaCoinRepository;

    public ValidaPilaService(RequisicoesService requisicoesService, DificuldadeService dificuldadeService, PilaCoinRepository pilaCoinRepository, LogController logController) {
        this.requisicoesService = requisicoesService;
        this.dificuldadeService = dificuldadeService;
        this.pilaCoinRepository=pilaCoinRepository;
        this.logController = logController;
    }

    @SneakyThrows
    @RabbitListener(queues = {"pila-minerado"})
    public void getMinerados(@Payload String strPila){

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                boolean pilaExistente = true;

//                for (String pila : ignorePilas) {
//                    if (pila.equals(strPila)) {
//                        pilaExistente = true;
//                        break;
//                    }
//                }

                if (pilaExistente) {
                    ignorePilas.add(strPila);

                    //System.out.println(ignorePilas);

                    ObjectMapper ob = new ObjectMapper();
                    PilaCoinJson pilaCoin = ob.readValue(strPila, PilaCoinJson.class);

                    if (pilaCoin.getNomeCriador().equals("joao_leo")) {
                        requisicoesService.enviarRequisicao("pila-minerado", strPila);
                        //pilaCoinRepository.save(pilaCoin);
                    } else {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        BigInteger hash = new BigInteger(md.digest(strPila.getBytes(StandardCharsets.UTF_8))).abs(); //Ver se não falta isso nas outras hashs

                        synchronized (dificuldadeService) {
                            if (dificuldadeService.getDif() == null) {
                                dificuldadeService.wait();
                            }
                        }

                        if (!(hash.compareTo(dificuldadeService.getDif()) > 0)) {

                            System.out.println("Validando pila do(a): " + pilaCoin.getNomeCriador());
                            logController.setLogsValidacaoPila("Validando pila do(a): " + pilaCoin.getNomeCriador());
                            md.reset();

                            Funcoes funcoes = new Funcoes();

                            Chaves chaves = new Chaves();

                            ValidacaoPilaJson validacaoPilaJson = ValidacaoPilaJson.builder().
                                    pilaCoinJson(pilaCoin).
                                    assinaturaPilaCoin(funcoes.geraAssinatura(strPila)).
                                    nomeValidador("joao_leo").
                                    chavePublicaValidador(chaves.getPublicKey().getEncoded())
                                    .build();
                            String jsonValidado = ob.writeValueAsString(validacaoPilaJson);
                            requisicoesService.enviarRequisicao("pila-validado", jsonValidado);
                            logController.setLogsValidacaoPila("Pila validado \nMinha assinatura: " + jsonValidado);
                            System.out.println("Minha assinatura: "+ jsonValidado);
                            System.out.println("Validado!");
                        } else {

                            requisicoesService.enviarRequisicao("pila-minerado", strPila);
                        }
                    }

                }
            }
        }).start();
    }

    public static PrivateKey readPrivateKeyFromFile(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    // Método para ler a chave pública de um arquivo
    public static PublicKey readPublicKeyFromFile(String fileName) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(fileName));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(keySpec);
    }
}
