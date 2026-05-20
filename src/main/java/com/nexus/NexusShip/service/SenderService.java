package com.nexus.NexusShip.service;

import com.nexus.NexusShip.dto.update.UserUpdateRequest;
import com.nexus.NexusShip.exception.NotFoundException;
import com.nexus.NexusShip.dto.request.SenderRequest;
import com.nexus.NexusShip.dto.response.SenderResponse;
import com.nexus.NexusShip.exception.AlreadyExistsException;
import com.nexus.NexusShip.mapper.SenderMapper;
import com.nexus.NexusShip.model.Sender;
import com.nexus.NexusShip.repository.SenderRepository;
import com.nexus.NexusShip.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
        Optional<Long> existingUserId = userRepository.findUserIdByNationalIdEverywhere(request.nationalId());
        if (existingUserId.isPresent()) {
            Long userId = existingUserId.get();
            Optional<Long> existingSender = senderRepository.findSenderIdByIdEveryWhere(userId);

            if (existingSender.isPresent()) {
                Sender sender = senderRepository.findSenderByIdEveryWhere(userId).get();
                if (!sender.isDeleted()) {
                    //The sender is an active sender
                    throw new AlreadyExistsException("A user with this National ID already exists as an active sender.");
                } else {
                    //The sender is deleted

                    userRepository.restoreUser(userId);
                    Sender restoredSender = senderRepository.findSenderByIdEveryWhere(userId)
                            .orElseThrow(() -> new NotFoundException("There is a problem during restore the deleted User."));
                    restoredSender.setDeleted(false);
                    return senderMapper.toResponse(senderRepository.save(restoredSender));

                }
            } else {
                userRepository.restoreUser(userId);
                return upgradeUserToSender(userId);
            }
        }

        //Check if the Email is exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new AlreadyExistsException("Email is already in use.");
        }

        //Map DTO to Entity

        Sender sender = senderMapper.toEntity(request);

        //Encode the password
        sender.setPassword(passwordEncoder.encode(request.password()));

        Sender savedSender = senderRepository.save(sender);

        //Map entity back to response
        return senderMapper.toResponse(savedSender);

    }

    @Transactional
    public SenderResponse upgradeUserToSender(Long userId) {
        senderRepository.insertSenderRole(userId);
        Sender upgradedSender = senderRepository.findSenderByIdEveryWhere(userId)
                .orElseThrow(() -> new NotFoundException("Error during sender role upgrade."));

        return senderMapper.toResponse(senderRepository.save(upgradedSender));
    }


    //Get all sender
    public List<SenderResponse> findAllSenders() {
        return senderRepository.findAll().stream().map(senderMapper::toResponse).toList();
    }

    //Get sender by id
    public SenderResponse findSenderById(Long id) {

        return senderRepository.findById(id)
                .map(senderMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("There is no user with ID " + id));

    }

    @Transactional
    public void deleteSender(Long id) {
        //Check if the id exist
        Sender sender = senderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no user with ID " + id));

        sender.setDeleted(true);
        senderRepository.save(sender);

    }

    @Transactional
    public SenderResponse updateSender(Long id, UserUpdateRequest request) {

        System.out.println("The received request is " + request);

        Sender sender = senderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sender not found with id: " + id));

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
                            throw new AlreadyExistsException("This phone number is already user by another user");
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

        return senderMapper.toResponse(senderRepository.save(sender));

    }


}

