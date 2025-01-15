package org.grnet.pidmr.enums;

public enum Validator {

    ISBN("The ISBN Validator is a utility designed to validate ISBN identifiers in both ISBN-10 and ISBN-13 formats. It ensures that the provided ISBN follows the correct format and that the checksum digit is valid according to the respective rules for each format."){
        @Override
        public boolean validate(String pid) {

            if (pid == null) return false;

            pid = pid.replace("-", "").trim();

            if (pid.length() == 10) {
                return isValidISBN10(pid);
            } else if (pid.length() == 13) {
                return isValidISBN13(pid);
            }
            return false;
        }

        // Method to validate ISBN-10
        private boolean isValidISBN10(String isbn) {

            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (isbn.charAt(i) - '0') * (i + 1);
            }

            char checkChar = isbn.charAt(9);
            int checkValue = (checkChar == 'X' || checkChar == 'x') ? 10 : (checkChar - '0');

            return sum % 11 == checkValue;
        }

        // Method to validate ISBN-13
        private boolean isValidISBN13(String isbn) {

            int sum = 0;
            for (int i = 0; i < 12; i++) {
                int digit = isbn.charAt(i) - '0';
                sum += (i % 2 == 0) ? digit : digit * 3;
            }

            int checkDigit = 10 - (sum % 10);
            if (checkDigit == 10) checkDigit = 0;

            return checkDigit == (isbn.charAt(12) - '0');
        }
    },
    NONE("No validation is performed. This validator is useful when validation is not required. It always returns true, regardless of the input."){
        @Override
        public boolean validate(String pid) {
            return true;
        }
    };

    private final String description;

    Validator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public abstract boolean validate(String pid);
}
