package br.ufsm.csi.pilacoin.repository;

import br.ufsm.csi.pilacoin.model.PilaCoinJson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PilaCoinRepository extends JpaRepository<PilaCoinJson, Long> {

    boolean existsByNonce(String nonce);

    @Transactional
    void deleteByNonce(String nonce);

    // Você pode adicionar métodos personalizados, se necessário
}

