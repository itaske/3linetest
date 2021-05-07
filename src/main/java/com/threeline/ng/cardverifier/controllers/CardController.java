package com.threeline.ng.cardverifier.controllers;

import com.threeline.ng.cardverifier.exceptions.BadRequestException;
import com.threeline.ng.cardverifier.models.Card;
import com.threeline.ng.cardverifier.responses.VerifyPayload;
import com.threeline.ng.cardverifier.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("card-scheme")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping("verify/{cardId}")
    public ResponseEntity<Map<String, Object>> getCardDetails(@PathVariable String cardId){
        if (cardId == null || cardId.length() < 6) {
            throw new BadRequestException("Invalid card number");
        }
        int formattedCardId;
        try {
            formattedCardId = Integer.parseInt(cardId.substring(0, 6));
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid card number");
        }

        Card card = cardService.verifyCard(formattedCardId);

        VerifyPayload payload = new VerifyPayload();
        payload.setBank(card.getBank());
        payload.setScheme(card.getScheme());
        payload.setType(card.getType());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("payload", payload);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("stats")
    public ResponseEntity<Map<String, Object>> getHitsCount(@RequestParam(defaultValue = "1") int start,
                                                            @RequestParam(defaultValue = "20") int limit){
        Map<String, Integer> payload = cardService.getCardHits(start, limit);
        long size = cardService.getRecordsSize();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("start", start);
        response.put("limit", limit);
        response.put("size", size);
        response.put("payload", payload);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
