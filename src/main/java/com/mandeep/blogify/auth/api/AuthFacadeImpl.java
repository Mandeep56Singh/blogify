package com.mandeep.blogify.auth.api;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.TokenView;
import com.mandeep.blogify.auth.application.command.AuthCommandService;
import com.mandeep.blogify.auth.application.dto.LoginRequest;
import com.mandeep.blogify.auth.application.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthCommandService authCommandService;

    @Override
    public TokenView login(String email, String password) {
        LoginResponse response = authCommandService.login(
                new LoginRequest(email, password)
        );

        return new TokenView(response.token(), response.expiresIn());
    }
}
