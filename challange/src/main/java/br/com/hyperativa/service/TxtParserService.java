package br.com.hyperativa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class TxtParserService {

    private static final Logger log = LoggerFactory.getLogger(TxtParserService.class);

    public static class ParseResult {
        public final List<String> cards = new ArrayList<>();
        public final List<String> rejected = new ArrayList<>();
    }

    public ParseResult parse(InputStream in) {
        ParseResult r = new ParseResult();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (line.startsWith("DESAFIO") || line.startsWith("LOTE")) continue;
                if (line.length() < 51) line = String.format("%-51s", line);

                try {
                    // Colunas fixas
                    String cardArea = line.substring(7, 26);  // área do cartão
                    String afterCardArea = line.substring(26, 51); // extra após cartão

                    // Detecta números fora da faixa permitida
                    if (afterCardArea.matches(".*\\d.*")) {
                        r.rejected.add("ln " + ln + " dígitos fora da área do cartão: '" + afterCardArea.trim() + "'");
                        continue;
                    }

                    // Extrai somente o cartão correto
                    String card = cardArea.replace(" ", "").trim();

                    if (card.matches("\\d{13,19}")) {
                        r.cards.add(card);
                    } else {
                        r.rejected.add("ln " + ln + " invalid card: '" + card + "' (len=" + card.length() + ")");
                    }
                } catch (Exception ex) {
                    r.rejected.add("ln " + ln + " parse error");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return r;
    }

}
