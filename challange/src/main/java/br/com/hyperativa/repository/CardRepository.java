package br.com.hyperativa.repository;

import br.com.hyperativa.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {

    boolean existsByCardHash(String cardHash);
    Optional<Card> findByCardHash(String cardHash);

}
