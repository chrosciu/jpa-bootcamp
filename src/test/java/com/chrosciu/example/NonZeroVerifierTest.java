package com.chrosciu.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class NonZeroVerifierTest {

    @Test
    public void shouldCopyNonZeroValue() {
        //given
        var nonZeroVerifier = new NonZeroVerifier();

        //when
        var result = nonZeroVerifier.verify(1);

        //then
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void shouldNotAcceptZeroValue() {
        //given
        var nonZeroVerifier = new NonZeroVerifier();

        //when /then
        assertThatThrownBy(() -> {
            nonZeroVerifier.verify(0);
        }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Zero");

    }

}
