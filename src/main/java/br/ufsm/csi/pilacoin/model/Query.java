package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Query {

    private long idQuery;
    private String nomeUsuario;
    private String status;
    private String usuarioMinerador;
    private String nonce;
    private long idBloco;
    private tiposQuery tipoQuery;

    public enum tiposQuery {
        PILA, BLOCO, USUARIOS
    }
}
