package com.mandeep.blogify.shared.infrastructure;

import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class IdGenerator {

    private IdGenerator() {}

    public static UUID next() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
