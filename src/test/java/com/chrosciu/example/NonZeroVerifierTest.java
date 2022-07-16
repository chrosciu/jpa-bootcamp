package com.chrosciu.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NonZeroVerifierTest {

    @Test
    public void shouldCopyNonZeroValue() {
        //given
        var nonZeroVerifier = new NonZeroVerifier();

        //when
        var result = nonZeroVerifier.verify(1);

        //then
        assertEquals(1, result);
    }

    @Test
    public void shouldNotAcceptZeroValue() {
        //given
        var nonZeroVerifier = new NonZeroVerifier();

        //when /then
        var exception = assertThrows(IllegalArgumentException.class, () -> {
            nonZeroVerifier.verify(0);
        }, "Exception not thrown");
        assertEquals("Zero is not allowed", exception.getMessage(), "Improper exception message");
//        try {
//            var result = nonZeroVerifier.verify(0);
//            Assertions.fail();
//        } catch (IllegalArgumentException e) {
//            Assertions.assertEquals("Zero is not allowed", e.getMessage());
//        }
    }

}
