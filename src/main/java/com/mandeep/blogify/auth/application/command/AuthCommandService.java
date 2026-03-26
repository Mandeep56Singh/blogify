package com.mandeep.blogify.auth.application.command;

import com.mandeep.blogify.auth.application.dto.*;
import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.domain.model.valueObject.Password;
import com.mandeep.blogify.auth.domain.repository.AuthRepository;
import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class AuthCommandService {

    private final AuthRepository authRepository;
    private final UserFacade userFacade;
    private final PasswordVerifier passwordVerifier;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public AuthCommandService(AuthRepository authRepository, UserFacade userFacade, PasswordVerifier passwordVerifier, PasswordHasher passwordHasher, TokenProvider tokenProvider) {
        this.authRepository = authRepository;
        this.userFacade = userFacade;
        this.passwordVerifier = passwordVerifier;
        this.passwordHasher = passwordHasher;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public LoginResponse signUp(SignUpRequest signUpRequest) {
        log.debug("signup.attempt email='{}' userName='{}'",
                signUpRequest.email(),
                signUpRequest.userName());

        String email = signUpRequest.email();
        String userName = signUpRequest.userName();
        HashedPassword hashedPassword = hashPassword(signUpRequest.password());

        UUID userId = userFacade.register(email, userName, hashedPassword.value(), Role.USER);
        log.info("signup.success id={} email='{}' userName='{}'",
                userId, email, userName);

        log.debug("token.generation.attempt id={} role='{}'", userId, Role.USER);

        return createLoginResponse(new AuthUserId(userId), Role.USER);
    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        log.debug("login.attempt email={}",
                loginRequest.email());


        //Checking for a valid email
        Email email;
        try {
            email = new Email(loginRequest.email());
        } catch (Exception ex) {
            throw CommonException.invalidCredentials();
        }

        String password = loginRequest.password();

        AuthenticatedUser user = authRepository.findByEmail(email).orElseThrow(
                CommonException::invalidCredentials
        );

        user.authenticate(password, passwordVerifier);

        return createLoginResponse(user.getAuthUserId(), user.getRole());
    }

    private HashedPassword hashPassword(String value) {
        Password password = new Password(value);
        return new HashedPassword(passwordHasher.hash(password.value()));
    }

    private LoginResponse createLoginResponse(AuthUserId id, Role role) {
        TokenInfo tokenInfo = tokenProvider.generateToken(id, role);

        AuthUserResponse authUserResponse = new AuthUserResponse(
                id.value(),
                role.name()
        );

        return new LoginResponse(
                tokenInfo.token(),
                "Bearer",
                tokenInfo.expiresIn(),
                authUserResponse
        );
    }

}
