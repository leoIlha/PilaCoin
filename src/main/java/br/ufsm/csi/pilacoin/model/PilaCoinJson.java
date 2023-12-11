package br.ufsm.csi.pilacoin.model;

//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Date;
//
//@AllArgsConstructor
//@Data
//@Builder
//@NoArgsConstructor
//@JsonPropertyOrder(alphabetic = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public class PilaCoinJson {
//
//    private Long id;
//    private Date dataCriacao;
//    private byte[] chaveCriador;
//    private String nomeCriador;
//    private String status;
//    private String nonce;
//
//
//}

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "pilacoin")
public class PilaCoinJson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dataCriacao;

    @Lob
    private byte[] chaveCriador;


    private String nomeCriador;
    private String status;
    private String nonce;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pilaCoinJson")
    private List<Transacao> transacoes;
}

