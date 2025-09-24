package com.leadqualification.controller;

import com.leadqualification.dto.OfferRequest;
import com.leadqualification.dto.OfferResponse;
import com.leadqualification.service.OfferService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class OfferController {

    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);

    private final OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping("/offer")
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody OfferRequest request) {
        logger.info("Received request to create offer: {}", request.getName());

        OfferResponse response = offerService.createOffer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/offer/{id}")
    public ResponseEntity<OfferResponse> getOffer(@PathVariable Long id) {
        logger.info("Received request to get offer with ID: {}", id);

        OfferResponse response = offerService.getOfferById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OfferResponse>> getAllOffers() {
        logger.info("Received request to get all offers");

        List<OfferResponse> responses = offerService.getAllOffers();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/offer/latest")
    public ResponseEntity<OfferResponse> getLatestOffer() {
        logger.info("Received request to get latest offer");

        OfferResponse response = offerService.getLatestOffer();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/offer/{id}")
    public ResponseEntity<OfferResponse> updateOffer(@PathVariable Long id, 
                                                    @Valid @RequestBody OfferRequest request) {
        logger.info("Received request to update offer with ID: {}", id);

        OfferResponse response = offerService.updateOffer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/offer/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        logger.info("Received request to delete offer with ID: {}", id);

        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}

