package br.ufsm.csi.pilacoin.utils;

import br.ufsm.csi.pilacoin.model.Chaves;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Funcoes {

    @SneakyThrows
    public byte[] geraAssinatura (String json) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hash = md.digest(json.getBytes(StandardCharsets.UTF_8));
        Cipher cipher = Cipher.getInstance("RSA");

        Chaves chaves = new Chaves();
        PrivateKey privateKey = chaves.getPrivateKey();

        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(hash);
    }
}
