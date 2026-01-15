package io.lunov.backend.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AccessCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            if (i == 4) {
                code.append("-");
            }
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return code.toString();
    }

    public boolean isValidFormat(String code) {
        if (code == null) {
            return false;
        }

        for (char c : code.toCharArray()) {
            if (CHARACTERS.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }
}
