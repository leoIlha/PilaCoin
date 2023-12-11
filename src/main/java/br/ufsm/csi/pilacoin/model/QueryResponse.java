package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryResponse {
    private long idQuery;
    private String usuario;
    @JsonProperty("pilasResult")
    private ArrayList<PilaCoinJson> pilasResult;
    private ArrayList<Bloco> blocosResult;
    private ArrayList<Usuario> usuariosResult;


    public ArrayList<PilaCoinJson> getPilasResult() {
        return pilasResult;
    }

}
