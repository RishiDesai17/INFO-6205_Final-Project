package edu.northeastern.psa.test;

import java.util.Random;

public class RandomNumberGenerator extends Random {
    private final Random random = new Random();

    @Override
    public double nextDouble() {
        return random.nextDouble();
    }
}
