package com.mandeep.blogify.auth.web.mapper;

import com.mandeep.blogify.auth.application.dto.LoginRequest;
import com.mandeep.blogify.auth.application.dto.LoginResponse;
import com.mandeep.blogify.auth.application.dto.SignUpRequest;
import com.mandeep.blogify.auth.web.dto.LoginWebRequest;
import com.mandeep.blogify.auth.web.dto.LoginWebResponse;
import com.mandeep.blogify.auth.web.dto.SignUpWebRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthWebMapper {
    LoginWebResponse toWebResponse(LoginResponse loginResponse);
    LoginRequest toLoginRequest(LoginWebRequest loginWebRequest);
    SignUpRequest toSignUpRequest(SignUpWebRequest signUpWebRequest);

}
