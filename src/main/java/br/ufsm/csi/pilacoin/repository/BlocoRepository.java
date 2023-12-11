package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlocoRepository extends JpaRepository<Bloco, Long> {
    boolean existsByNonce(String nonce);

}
