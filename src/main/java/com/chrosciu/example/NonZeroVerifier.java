package com.chrosciu.example;

public class NonZeroVerifier {
    public int verify(int input) {
        if (input != 0) {
            return input;
        } else {
            throw new IllegalArgumentException("Zero is not allowed here");
        }
    }
}
