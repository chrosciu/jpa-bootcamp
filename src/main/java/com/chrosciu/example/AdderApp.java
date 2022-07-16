package com.chrosciu.example;

public class AdderApp {

    public static void main(String[] args) {
        Adder adder = new Adder();
        var result = adder.sum(2, 3);
        if (result != 5) {
            throw new RuntimeException("Adder is not working");
        }
        System.out.println(result);
    }
}
