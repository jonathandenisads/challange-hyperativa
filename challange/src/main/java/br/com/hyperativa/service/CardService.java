package br.com.hyperativa.service;

import br.com.hyperativa.model.Card;
import br.com.hyperativa.repository.CardRepository;
import br.com.hyperativa.util.AesUtil;
import br.com.hyperativa.util.HmacUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CardService {

    @Autowired
    private  CardRepository repository;

    @Autowired
    private HmacUtil hmac;

    @Autowired
    private AesUtil aes;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(CardService.class);

    public Card populateObject(String cards, String hash, String enc, String source){
        Card card = new Card();
        card.setId(UUID.randomUUID().toString());
        card.setCardHash(hash);
        card.setCardEnc(enc);
        card.setLast4(cards.substring(cards.length() - 4));
        card.setSource(source);
        card.setCreatedAt(LocalDateTime.now());
        return card;
    }

    public String storeCard(String cardNumber, String source) {
        String hash = hmac.hmacSha256(cardNumber);
        if (repository.existsByCardHash(hash)) {
            return repository.findByCardHash(hash).map(Card::getId).orElse(null);
        }
        String enc = aes.encrypt(cardNumber);

        var card = populateObject(cardNumber, hash, enc, source);
        repository.save(card);
        return card.getId();
    }

    public Map<String,Object> storeBatch(List<String> cards, List<String> rejected, String source) {
        log.info("Processamento iniciando | origem={} | totalRecebido={}", source, cards.size());

        int batchSize = 5000;
        List<Card> batch = new ArrayList<>();
        int inserted = 0;

        for (String c : cards) {
            try {
                String hash = hmac.hmacSha256(c);
                String redisKey = "card:" + hash;
                boolean existsInCache = Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
                if (!existsInCache) {
                    if (!repository.existsByCardHash(hash)) {
                        String enc = aes.encrypt(c);
                        var card = populateObject(c, hash, enc, source);
                        batch.add(card);
                        redisTemplate.opsForValue().set(redisKey, "true");
                    } else {
                        log.debug("Cartão já existente no banco | valorMasked=****{}", c.substring(c.length() - 4));
                    }
                }else {
                    log.debug("Cartão já existente em cache | ****{}", c.substring(c.length() - 4));
                }

            } catch (Exception e) {
                String masked = (c != null && c.length() > 4) ? c.substring(c.length() - 4) : "XXXX";
                log.warn("Falha ao processar cartão | valorMasked=****{} | erro={}", masked, e.getMessage());
                //rejected.add(c + ":" + e.getMessage());
            }

            if (batch.size() >= batchSize) {
                repository.saveAll(batch);
                log.info("Lote gravado no banco | quantidade={} | origem={}", batch.size(), source);
                inserted += batch.size();
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            repository.saveAll(batch);
            inserted += batch.size();
        }
        log.info("Processamento finalizado | totalInserido={} | totalRejeitado={} | origem={}",
                inserted, rejected.size(), source);
        return Map.of("inserted", inserted, "rejected", rejected);
    }

    public Optional<String> exists(String cardNumber) {
        String hash = hmac.hmacSha256(cardNumber);
        return repository.findByCardHash(hash).map(Card::getId);
    }

}
