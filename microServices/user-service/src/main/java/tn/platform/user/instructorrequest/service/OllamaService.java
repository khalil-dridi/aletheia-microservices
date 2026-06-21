package tn.platform.user.instructorrequest.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    public String analyzeText(String text) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:11434/api/generate";

        String prompt = """
Tu es un recruteur IT.

Analyse ce CV et donne une réponse très courte (maximum 3 lignes) :

Format obligatoire:
Score: X/10
Decision: ACCEPT ou REJECT
Résumé: une seule phrase courte

CV:
""" + text;

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody().get("response").toString();
    }
}