package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.exception.UserNotFound;
import com.nexus.NexusShip.dto.request.SenderRequest;
import com.nexus.NexusShip.dto.response.SenderResponse;
import com.nexus.NexusShip.exception.UserAlreadyExists;
import com.nexus.NexusShip.mapper.SenderMapper;
import com.nexus.NexusShip.model.Sender;
import com.nexus.NexusShip.repository.SenderRepository;
import com.nexus.NexusShip.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SenderService {

    private final SenderRepository senderRepository;
    private final SenderMapper senderMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SenderService(SenderRepository senderRepository, SenderMapper senderMapper,
                         UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.senderRepository = senderRepository;
        this.senderMapper = senderMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Create new sender
    @Transactional
    public SenderResponse registerSender(SenderRequest request) {

        //Check If the nationalID is Exist
        if (userRepository.findByNationalId(request.nationalId()).isPresent()) {
            throw new UserAlreadyExists("A user with this National ID already exits.");
        }

        //Check if the Email is exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExists("Email is already in use.");
        }

        //Map DTO to Entity

        Sender sender = senderMapper.toEntity(request);

        //Encode the password
        sender.setPassword(passwordEncoder.encode(request.password()));

        Sender savedSender = senderRepository.save(sender);

        //Map entity back to response
        return senderMapper.toResponse(savedSender);

    }


    //Get all sender
    public List<SenderResponse> getAllSenders() {
        return senderRepository.findAll().stream().map(senderMapper::toResponse).toList();
    }

    //Get sender by id
    public SenderResponse getSenderById(Long id) {

        return senderRepository.findById(id)
                .map(senderMapper::toResponse)
                .orElseThrow(() -> new UserNotFound("There is no user with ID " + id));

    }

    @Transactional
    public void deleteSender(Long id) {
        //Check if the id exist
        Sender sender = senderRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("There is no user with ID " + id));

        sender.setDeleted(true);
        senderRepository.save(sender);

    }

    @Transactional
    public SenderResponse updateSender(Long id, UserUpdateRequest request) {

        Sender sender = senderRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("Sender not found with id: " + id));

        if (request.firstName() != null && !request.firstName().isBlank()) {
            sender.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            sender.setLastName(request.lastName());
        }

        if (request.phoneNumber() != null) {
            userRepository.findByPhoneNumber(request.phoneNumber())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new UserAlreadyExists("This phone number is already user by another user");
                        }
                    });
            sender.setPhoneNumber(request.phoneNumber());
        }

        if (request.password() != null && !request.password().isBlank()) {
            sender.setPassword(passwordEncoder.encode(request.password()));
        }
        if (request.gender() != null) {
            sender.setGender(request.gender());
        }

        Sender updatedSender = senderRepository.save(sender);
        return senderMapper.toResponse(updatedSender);

    }


}

