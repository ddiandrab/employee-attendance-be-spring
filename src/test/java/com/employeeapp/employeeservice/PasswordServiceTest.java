package com.employeeapp.employeeservice;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordServiceTest {

    @Test
    void shouldVerifyArgon2Hash() {

        Argon2 argon2 = Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id
        );

        String password = "PASSWORD_TEST";
        String hash = "HASH_LENGKAP_DARI_DATABASE";

        boolean matches = argon2.verify(
                hash,
                password.toCharArray()
        );

        assertTrue(matches);
    }
}