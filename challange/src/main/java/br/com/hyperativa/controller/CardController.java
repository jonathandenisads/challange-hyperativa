package br.com.hyperativa.controller;

import br.com.hyperativa.service.CardService;
import br.com.hyperativa.service.TxtParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private TxtParserService parser;

    @PostMapping
    public ResponseEntity<?> addOne(@RequestBody Map<String,String> body) {
        String card = body.get("cardNumber");
        if (card == null || !card.matches("\\d{13,19}")) {
            return ResponseEntity.badRequest().body(Map.of("error","invalid_card"));
        }
        String id = cardService.storeCard(card, "manual");
        return ResponseEntity.status(201).body(Map.of("id", id));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file) throws Exception {
        var result = parser.parse(file.getInputStream());
        var res = cardService.storeBatch(result.cards,result.rejected, "file_upload");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/exists")
    public ResponseEntity<?> exists(@RequestBody Map<String,String> body) {
        String card = body.get("cardNumber");
        if (card == null || !card.matches("\\d{13,19}")) {
            return ResponseEntity.badRequest().body(Map.of("exists", false));
        }
        var opt = cardService.exists(card);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", opt.isPresent());
        response.put("id", opt.orElse(null));

        return ResponseEntity.ok(response);
    }

}
