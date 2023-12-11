package br.ufsm.csi.pilacoin.service;

import br.ufsm.csi.pilacoin.controller.LogController;
import br.ufsm.csi.pilacoin.model.Bloco;
import br.ufsm.csi.pilacoin.model.Chaves;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

@Service
public class MineracaoBlocoService {

    private DificuldadeService dificuldadeService;
    private RequisicoesService requisicoesService;
    private LogController logController;

    public MineracaoBlocoService(DificuldadeService dificuldadeService, RequisicoesService requisicoesService, LogController logController) {
        this.dificuldadeService = dificuldadeService;
        this.requisicoesService = requisicoesService;
        this.logController = logController;
    }

    @RabbitListener(queues = {"descobre-bloco"})
    public void iniciaMineracao (@Payload String strBloco) {
        Chaves chaves = new Chaves();
        PrivateKey privateKey = chaves.getPrivateKey();
        PublicKey publicKey =  chaves.getPublicKey();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {

                synchronized (dificuldadeService) {
                    if (dificuldadeService.getDif() == null) {
                        dificuldadeService.wait();
                    }
                }

                ObjectMapper ob = new ObjectMapper();
                Bloco bloco = ob.readValue(strBloco, Bloco.class);

                //bloco.setChaveUsuarioMinerador(publicKey.toString().getBytes(StandardCharsets.UTF_8));
                bloco.setChaveUsuarioMinerador(publicKey.getEncoded());
                bloco.setNomeUsuarioMinerador("joao_leo");

                BigInteger hash;
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                int total = 0;
                do {
                    Random random = new Random();
                    byte[] byteArray = new byte[256/8];
                    random.nextBytes(byteArray);

                    bloco.setNonce(new BigInteger(byteArray).abs().toString());

                    ObjectMapper om = new ObjectMapper();

                    String json = om.writeValueAsString(bloco);
                    hash = new BigInteger(md.digest(json.getBytes(StandardCharsets.UTF_8))).abs();
                    total++;

                } while (hash.compareTo(dificuldadeService.getDif()) > 0);
                //achou!!!!
                System.out.println("Bloco minerado com "+total+" tentativas");

                ObjectMapper om = new ObjectMapper();

                //System.out.println(om.writeValueAsString(bloco));

                requisicoesService.enviarRequisicao("bloco-minerado", om.writeValueAsString(bloco));
                logController.setLogsMineracaoBloco("Bloco minerado com "+total+" tentativas");
            }
        }).start();
    }
}
