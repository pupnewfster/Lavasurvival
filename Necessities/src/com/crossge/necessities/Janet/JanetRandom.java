package com.crossge.necessities.Janet;

import java.util.Random;

public class JanetRandom extends Random {
	private static final long serialVersionUID = 1L;

	public int rInt(int m) {//Returns a random int from 0 - m
        return (int) ((System.currentTimeMillis() + nextInt()) % m);
    }
}