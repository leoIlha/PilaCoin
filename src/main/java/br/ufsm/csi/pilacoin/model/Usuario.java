package br.ufsm.csi.pilacoin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private byte[] chavePublica;
    @Column(unique = true, nullable = false)
    private String nome;
}
