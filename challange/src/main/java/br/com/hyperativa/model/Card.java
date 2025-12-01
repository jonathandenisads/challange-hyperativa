package br.com.hyperativa.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cards", indexes = {@Index(name = "idx_card_hash", columnList = "card_hash")})
public class Card {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "card_hash",  nullable = false, length = 128, unique = true)
    private String cardHash;

    @Column(name = "card_enc", nullable = false, columnDefinition = "LONGTEXT")
    private String cardEnc;

    @Column(nullable = false, length = 4)
    private String last4;

    @Column(length = 50)
    private String source;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Card() {
    }

    public Card(String id, String cardHash, String cardEnc, String last4, String source) {
        this.id = id;
        this.cardHash = cardHash;
        this.cardEnc = cardEnc;
        this.last4 = last4;
        this.source = source;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardHash() {
        return cardHash;
    }

    public void setCardHash(String cardHash) {
        this.cardHash = cardHash;
    }

    public String getCardEnc() {
        return cardEnc;
    }

    public void setCardEnc(String cardEnc) {
        this.cardEnc = cardEnc;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
