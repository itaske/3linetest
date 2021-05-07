package com.threeline.ng.cardverifier.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threeline.ng.cardverifier.exceptions.OperationFailedException;
import com.threeline.ng.cardverifier.models.Card;
import com.threeline.ng.cardverifier.models.Hit;
import com.threeline.ng.cardverifier.repositories.HitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private HitRepository hitRepository;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Verify the card using the numbers given as input it caches the values in h2 database and use it for next same values
     * and the return card with its details
     * @param cardId
     * @return card
     */
    public Card verifyCard(int cardId) {

        AtomicReference<Card> cardReference = new AtomicReference<>(new Card());

        //checks if it is available and uses the past value
        hitRepository.findByCardId(cardId).ifPresent(hit -> {
            hit.setHitCount(hit.getHitCount() + 1);
            hitRepository.save(hit);

            cardReference.get().setScheme(hit.getScheme());
            cardReference.get().setType(hit.getType());
            cardReference.get().setBank(hit.getBank());
        });

        if (cardReference.get().getScheme() != null)
            return cardReference.get();

        String verifyUrl = "https://lookup.binlist.net/";
        ResponseEntity<String> response = restTemplate.getForEntity(verifyUrl + cardId, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new OperationFailedException("Card verification failed");
        }
        cardReference.get().setScheme(root.path("scheme").asText());
        cardReference.get().setType(root.path("type").asText());
        JsonNode bank = root.path("bank");
        cardReference.get().setBank(bank.path("name").asText());

        Hit newHit = createHit(cardId, cardReference);
        hitRepository.save(newHit);

        return cardReference.get();
    }

    private Hit createHit(int cardId, AtomicReference<Card> cardReference) {
        Hit newHit = new Hit();
        newHit.setHitCount(1);
        newHit.setBank(cardReference.get().getBank());
        newHit.setCardId(cardId);
        newHit.setScheme(cardReference.get().getScheme());
        newHit.setType(cardReference.get().getType());
        return newHit;
    }

    /***
     * Get the total hits on all the available cards
     * @param start
     * @param limit
     * @return result
     */
    public Map<String, Integer> getCardHits(int start, int limit) {
        Pageable pageable = PageRequest.of((start - 1), limit);
        Map<String, Integer> result;
        try{
            result = hitRepository.findAll(pageable).stream()
                    .collect(Collectors.toMap((hit) -> hit.getCardId().toString(), Hit::getHitCount));
        } catch(Exception e){
            throw new OperationFailedException("Unable to retrieve Card stats");
        }
        return result;
    }

    public long getRecordsSize() {
        return hitRepository.count();
    }
}
