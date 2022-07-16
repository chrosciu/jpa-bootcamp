package com.chrosciu.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdderTest {

    @Test
    public void shouldAddTwoNumbers() {
        //given
        var adder = new Adder();

        //when
        var result = adder.sum(2, 3);

        //then
        Assertions.assertEquals(5, result);
    }

    @Test
    public void shouldAddOtherTwoNumbers() {
        //given
        var adder = new Adder();

        //when
        var result = adder.sum(4, 5);

        //then
        Assertions.assertEquals(9, result);
    }

}
