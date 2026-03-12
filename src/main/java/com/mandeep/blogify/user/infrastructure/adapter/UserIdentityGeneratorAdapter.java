package com.mandeep.blogify.user.infrastructure.adapter;

import com.mandeep.blogify.shared.infrastructure.IdGenerator;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.repository.UserIdentityGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserIdentityGeneratorAdapter implements UserIdentityGenerator {
    @Override
    public UserId nextUserId() {
        return new UserId(IdGenerator.next());
    }
}
