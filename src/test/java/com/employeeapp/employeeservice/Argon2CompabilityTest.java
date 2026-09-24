package com.employeeapp.employeeservice;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Argon2CompatibilityTest {

    @Test
    void shouldVerifyNodeArgon2Hash() {

        String password = "TestPassword123!";

        String nodeHash = "$argon2id$v=19$m=65536,p=4,t=3$Wc6ElBPKsUPyu1AH90UHjQ$2Old51h89QuqdiH3lHq9ngK9QocnGdpUzTdahqEIbtA";

        String normalizedHash = normalizeNodeArgon2Hash(nodeHash);

        System.out.println("Original  : " + nodeHash);
        System.out.println("Normalized: " + normalizedHash);

        Argon2 argon2 = Argon2Factory.create(
                Argon2Factory.Argon2Types.ARGON2id);

        boolean result = argon2.verify(
                normalizedHash,
                password.toCharArray());

        System.out.println("Java verify: " + result);

        assertTrue(result);
    }

    private String normalizeNodeArgon2Hash(String hash) {

        String[] parts = hash.split("\\$");

        // ['', 'argon2id', 'v=19',
        // 'm=65536,p=4,t=3', 'salt', 'hash']

        if (parts.length != 6) {
            throw new IllegalArgumentException(
                    "Invalid Argon2 hash format");
        }

        String[] params = parts[3].split(",");

        String memory = null;
        String iterations = null;
        String parallelism = null;

        for (String param : params) {

            if (param.startsWith("m=")) {
                memory = param;
            } else if (param.startsWith("t=")) {
                iterations = param;
            } else if (param.startsWith("p=")) {
                parallelism = param;
            }
        }

        if (memory == null
                || iterations == null
                || parallelism == null) {
            throw new IllegalArgumentException(
                    "Invalid Argon2 parameters");
        }

        return "$"
                + parts[1]
                + "$"
                + parts[2]
                + "$"
                + memory
                + ","
                + iterations
                + ","
                + parallelism
                + "$"
                + parts[4]
                + "$"
                + parts[5];
    }
}