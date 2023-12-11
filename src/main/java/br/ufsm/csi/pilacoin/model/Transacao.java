package br.ufsm.csi.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "transacaofinal")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1000)
    private String chaveUsuarioOrigem;
    @Column(length = 1000)
    private String chaveUsuarioDestino;
    @Column(length = 1000)
    private String assinatura;
    @Column(length = 1000)
    private String noncePila;
    @Temporal(TemporalType.DATE)
    private Date dataTransacao;

    private String status;

    @ManyToOne
    @JoinColumn(name = "id_pila")
    private PilaCoinJson pilaCoinJson;

    @ManyToOne
    @JoinColumn(name = "id_bloco")
    private Bloco bloco;
}
