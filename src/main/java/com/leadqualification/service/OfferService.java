package com.leadqualification.service;

import com.leadqualification.dto.OfferRequest;
import com.leadqualification.dto.OfferResponse;
import com.leadqualification.entity.Offer;
import com.leadqualification.exception.ResourceNotFoundException;
import com.leadqualification.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OfferService {

    private static final Logger logger = LoggerFactory.getLogger(OfferService.class);

    private final OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public OfferResponse createOffer(OfferRequest request) {
        logger.info("Creating new offer: {}", request.getName());

        Offer offer = new Offer(
            request.getName(),
            request.getValueProps(),
            request.getIdealUseCases()
        );

        Offer savedOffer = offerRepository.save(offer);
        logger.info("Offer created successfully with ID: {}", savedOffer.getId());

        return new OfferResponse(savedOffer);
    }

    @Transactional(readOnly = true)
    public OfferResponse getOfferById(Long id) {
        logger.info("Retrieving offer with ID: {}", id);

        Offer offer = offerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));

        return new OfferResponse(offer);
    }

    @Transactional(readOnly = true)
    public List<OfferResponse> getAllOffers() {
        logger.info("Retrieving all offers");

        return offerRepository.findAll()
            .stream()
            .map(OfferResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OfferResponse getLatestOffer() {
        logger.info("Retrieving latest offer");

        Offer offer = offerRepository.findLatestOffer()
            .orElseThrow(() -> new ResourceNotFoundException("No offers found"));

        return new OfferResponse(offer);
    }

    public OfferResponse updateOffer(Long id, OfferRequest request) {
        logger.info("Updating offer with ID: {}", id);

        Offer offer = offerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Offer not found with ID: " + id));

        offer.setName(request.getName());
        offer.setValueProps(request.getValueProps());
        offer.setIdealUseCases(request.getIdealUseCases());

        Offer updatedOffer = offerRepository.save(offer);
        logger.info("Offer updated successfully with ID: {}", updatedOffer.getId());

        return new OfferResponse(updatedOffer);
    }

    public void deleteOffer(Long id) {
        logger.info("Deleting offer with ID: {}", id);

        if (!offerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Offer not found with ID: " + id);
        }

        offerRepository.deleteById(id);
        logger.info("Offer deleted successfully with ID: {}", id);
    }
}

