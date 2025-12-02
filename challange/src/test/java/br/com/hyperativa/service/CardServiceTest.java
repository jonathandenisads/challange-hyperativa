package br.com.hyperativa.service;

import br.com.hyperativa.model.Card;
import br.com.hyperativa.repository.CardRepository;
import br.com.hyperativa.util.AesUtil;
import br.com.hyperativa.util.HmacUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository repository;

    @Mock
    private HmacUtil hmac;

    @Mock
    private AesUtil aes;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void storeCard_shouldReturnExistingId_whenCardExists() {
        String cardNumber = "1234567890123";
        String hash = "hash123";
        String existingId = "card-1";

        when(hmac.hmacSha256(cardNumber)).thenReturn(hash);
        when(repository.existsByCardHash(hash)).thenReturn(true);
        when(repository.findByCardHash(hash)).thenReturn(Optional.of(new Card(){{
            setId(existingId);
        }}));

        String result = cardService.storeCard(cardNumber, "manual");

        assertEquals(existingId, result);
        verify(repository, never()).save(any());
    }

    @Test
    void storeCard_shouldSaveNewCard_whenCardNotExists() {
        String cardNumber = "1234567890123";
        String hash = "hash123";
        String encrypted = "encrypted123";

        when(hmac.hmacSha256(cardNumber)).thenReturn(hash);
        when(repository.existsByCardHash(hash)).thenReturn(false);
        when(aes.encrypt(cardNumber)).thenReturn(encrypted);
        when(repository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String id = cardService.storeCard(cardNumber, "manual");

        assertNotNull(id);
        verify(repository).save(any(Card.class));
    }

    @Test
    void exists_shouldReturnId_whenCardExists() {
        String cardNumber = "1234567890123";
        String hash = "hash123";
        String id = "card-1";

        when(hmac.hmacSha256(cardNumber)).thenReturn(hash);
        when(repository.findByCardHash(hash)).thenReturn(Optional.of(new Card(){{
            setId(id);
        }}));

        Optional<String> result = cardService.exists(cardNumber);

        assertTrue(result.isPresent());
        assertEquals(id, result.get());
    }

    @Test
    void exists_shouldReturnEmpty_whenCardNotExists() {
        String cardNumber = "1234567890123";
        String hash = "hash123";

        when(hmac.hmacSha256(cardNumber)).thenReturn(hash);
        when(repository.findByCardHash(hash)).thenReturn(Optional.empty());

        Optional<String> result = cardService.exists(cardNumber);

        assertTrue(result.isEmpty());
    }

    @Test
    void storeBatch_shouldSaveOnlyNonExistingCards() {
        List<String> cards = List.of("1111222233334444", "5555666677778888");
        List<String> rejected = new ArrayList<>();
        String source = "file_upload";

        when(hmac.hmacSha256(anyString())).thenReturn("hash1", "hash2");
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.existsByCardHash(anyString())).thenReturn(false);
        when(aes.encrypt(anyString())).thenReturn("enc1", "enc2");

        Map<String, Object> result = cardService.storeBatch(cards, rejected, source);

        assertEquals(2, result.get("inserted"));
        assertEquals(rejected, result.get("rejected"));

        verify(repository, atLeastOnce()).saveAll(anyList());
    }
    @Test
    void storeBatch_shouldHandleAllCases() {
        List<String> cards = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        String source = "file_upload";

        // 3 cartões simulados
        String validCard = "1111222233334444";
        String inDbCard = "5555666677778888";
        String inRedisCard = "9999000011112222";
        String exceptionCard = "0000111122223333";

        cards.add(validCard);
        cards.add(inDbCard);
        cards.add(inRedisCard);
        cards.add(exceptionCard);

        // Mocks HMAC
        when(hmac.hmacSha256(validCard)).thenReturn("hash_valid");
        when(hmac.hmacSha256(inDbCard)).thenReturn("hash_db");
        when(hmac.hmacSha256(inRedisCard)).thenReturn("hash_redis");
        when(hmac.hmacSha256(exceptionCard)).thenReturn("hash_exception");

        // Redis
        when(redisTemplate.hasKey("card:hash_valid")).thenReturn(false);
        when(redisTemplate.hasKey("card:hash_db")).thenReturn(false);
        when(redisTemplate.hasKey("card:hash_redis")).thenReturn(true); // já existe em cache
        when(redisTemplate.hasKey("card:hash_exception")).thenReturn(false);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Banco
        when(repository.existsByCardHash("hash_valid")).thenReturn(false);
        when(repository.existsByCardHash("hash_db")).thenReturn(true); // já existe no DB
        when(repository.existsByCardHash("hash_exception")).thenReturn(false);

        // AES
        when(aes.encrypt(validCard)).thenReturn("enc_valid");
        when(aes.encrypt(exceptionCard)).thenThrow(new RuntimeException("crypto error")); // lança exceção

        // Salvar batch
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = cardService.storeBatch(cards, rejected, source);

        // Valid card foi inserido
        assertEquals(1, result.get("inserted"));

        // Verifica chamadas
        verify(repository).saveAll(argThat(iterable -> {
            List<Card> list = new ArrayList<>();
            iterable.forEach(list::add); // converte Iterable em List
            return list.size() == 1 && list.get(0).getCardHash().equals("hash_valid");
        }));     verify(valueOperations).set("card:hash_valid", "true");
    }

    @Test
    void storeBatch_shouldSplitLargeBatchIntoMultipleSaves() {
        List<String> cards = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        String source = "file_upload";

        // 5001 cartões para testar divisão em lotes
        for (int i = 0; i < 5001; i++) {
            cards.add(String.format("400000000000%03d", i));
        }

        when(hmac.hmacSha256(anyString())).thenAnswer(invocation -> "hash" + invocation.getArgument(0));
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(repository.existsByCardHash(anyString())).thenReturn(false);
        when(aes.encrypt(anyString())).thenReturn("enc");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(repository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = cardService.storeBatch(cards, rejected, source);

        assertEquals(5001, result.get("inserted"));
        assertEquals(0, ((List<?>) result.get("rejected")).size());

        // Verifica se saveAll foi chamado duas vezes (5000 + 1)
        verify(repository, times(2)).saveAll(anyList());
    }
}
