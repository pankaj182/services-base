package com.kpa.test.common.utils;

public class MathUtils {
  public static int getRandomNumber(int min, int max) {
    return (int) (Math.random() * (max - min + 1) + min);
  }
}
