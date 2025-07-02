package com.eaglebank.feature.auth.service;

import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.auth.repository.IdentityRepository;
import com.eaglebank.feature.auth.repository.domain.Identity;
import com.eaglebank.feature.auth.web.model.LoginRequest;
import com.eaglebank.feature.common.exception.IdentityException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IdentityService {
    private final IdentityRepository identityRepository;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IdentityService(IdentityRepository identityRepository,
                           JwtProvider jwtProvider,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.identityRepository = identityRepository;
        this.jwtProvider = jwtProvider;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void createIdentity(UUID userId, Identity identity) {
        identityRepository.createIdentity(userId, identity.getEmail(), bCryptPasswordEncoder.encode(identity.getPassword()));
    }

    public String login(final LoginRequest loginRequest) {
        List<Identity> identities = identityRepository.getIdentityByEmail(loginRequest.getEmail());
        if (identities.isEmpty()) {
            throw new IdentityException("Invalid email or password");
        }

        Identity identity = identities.getFirst();
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), identity.getPassword())) {
            throw new IdentityException("Invalid email or password");
        }

        return jwtProvider.generateToken(identity.getUserId());
    }
}
