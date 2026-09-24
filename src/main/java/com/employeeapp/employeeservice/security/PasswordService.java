package com.employeeapp.employeeservice.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

        private final Argon2 argon2 = Argon2Factory.create(
                        Argon2Factory.Argon2Types.ARGON2id);

        public boolean matches(
                        String rawPassword,
                        String passwordHash) {
                String normalizedHash = normalizeArgon2Hash(passwordHash);

                return argon2.verify(
                                normalizedHash,
                                rawPassword.toCharArray());
        }


        // Normalizes the Argon2 hash to ensure compatibility with the Java Argon2 library.
        // Format: $argon2id$v=19$m=65536,t=3,p=4$<salt>$<hash>
        private String normalizeArgon2Hash(String hash) {

                String[] parts = hash.split("\\$");

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