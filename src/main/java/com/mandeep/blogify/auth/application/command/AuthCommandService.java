package com.mandeep.blogify.auth.application.command;

import com.mandeep.blogify.auth.application.dto.*;
import com.mandeep.blogify.auth.domain.exception.AuthDomainException;
import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.Email;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.domain.model.valueObject.Password;
import com.mandeep.blogify.auth.domain.repository.AuthRepository;
import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final AuthRepository authRepository;
    private final UserFacade userFacade;
    private final PasswordVerifier passwordVerifier;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    @Transactional
    public LoginResponse signUp(SignUpRequest signUpRequest) {
        log.debug("signup.attempt email='{}' userName='{}'",
                signUpRequest.email(),
                signUpRequest.userName());

        String email = signUpRequest.email();
        String userName = signUpRequest.userName();
        HashedPassword hashedPassword = hashPassword(signUpRequest.password());

        userFacade.register(email, userName, hashedPassword.value(), Role.USER);
        log.info("signup.success email='{}' userName='{}'",
                signUpRequest.email(),
                signUpRequest.userName());
        
        return login(new LoginRequest(email, signUpRequest.password()));
    }

    @Transactional
    public void signUpAdmin(SignUpRequest signUpRequest) {
        log.debug("admin.signup.attempt email='{}' userName='{}'",
                signUpRequest.email(),
                signUpRequest.userName());

        String email = signUpRequest.email();
        String userName = signUpRequest.userName();
        HashedPassword hashedPassword = hashPassword(signUpRequest.password());

        userFacade.register(email, userName, hashedPassword.value(), Role.ADMIN);
        log.info("admin.signup.success email='{}' userName='{}'",
                signUpRequest.email(),
                signUpRequest.userName());

    }

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {

        log.debug("login.attempt email={}",
                loginRequest.email());

        Email email = new Email(loginRequest.email());
        String password = loginRequest.password();

        AuthenticatedUser user = authRepository.findByEmail(email).orElseThrow(
                AuthDomainException::invalidCredentials
        );

        user.authenticate(password, passwordVerifier);

        AuthUserResponse authUserResponse = new AuthUserResponse(
                user.getAuthUserId().value(),
                user.getRole().name()
        );

        TokenInfo tokenInfo = tokenProvider.generateToken(user.getAuthUserId(), user.getRole());

        log.debug("login.success email='{}'", email.value());
        return new LoginResponse(
                tokenInfo.token(),
                "Bearer",
                tokenInfo.expiresIn(),
                authUserResponse
        );
    }

    private HashedPassword hashPassword(String value) {
        Password password = new Password(value);
        return new HashedPassword(passwordHasher.hash(password.value()));
    }

}
