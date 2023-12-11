package br.ufsm.csi.pilacoin.controller;

import br.ufsm.csi.pilacoin.model.Usuario;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/log")
@CrossOrigin
public class LogController {

    private ArrayList<String> logsMineracaoPila = new ArrayList<>();
    private ArrayList<String> logsMineracaoBloco = new ArrayList<>();
    private ArrayList<String> logsValidacaoPila = new ArrayList<>();
    private ArrayList<String> logsValidacaoBloco = new ArrayList<>();
    private ArrayList<String> logsMensagens = new ArrayList<>();

    public void setLogsMineracaoPila(String log) {
        this.logsMineracaoPila.add(log);
    }

    public void setLogsMineracaoBloco(String log) {
        this.logsMineracaoBloco.add(log);
    }

    public void setLogsValidacaoPila(String log) {
        this.logsValidacaoPila.add(log);
    }

    public void setLogsValidacaoBloco(String log) {
        this.logsValidacaoBloco.add(log);
    }

    public void setLogsMensagens(String log) {
        this.logsMensagens.add(log);
    }

        @GetMapping("/logMineracaoPila")
    public ArrayList<String> getLogMineracaoPila() {
        return logsMineracaoPila;
    }

    @GetMapping("/logMineracaoBloco")
    public ArrayList<String> getLogMineracaoBloco() {
        return logsMineracaoBloco;
    }

    @GetMapping("/logValidacaoPila")
    public ArrayList<String> getLogsValidacaoPila() {
        return logsValidacaoPila;
    }

    @GetMapping("/logValidacaoBloco")
    public ArrayList<String> getLogsValidacaoBloco() {
        return logsValidacaoBloco;
    }

    @GetMapping("/logMensagens")
    public ArrayList<String> getLogsMensagens() {
        return logsMensagens;
    }
}
