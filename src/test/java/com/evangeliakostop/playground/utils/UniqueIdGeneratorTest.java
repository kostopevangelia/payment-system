package com.evangeliakostop.playground.utils;

import com.evangeliakostop.playground.utils.UniqueIdGenerator;
import org.junit.jupiter.api.Test;

class UniqueIdGeneratorTest {

    @Test
    void generateSecureToken() {
        String token = UniqueIdGenerator.generateSecureToken();
        System.out.println(token);
    }
}