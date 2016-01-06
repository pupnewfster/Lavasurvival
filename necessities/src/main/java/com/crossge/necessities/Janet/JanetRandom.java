package com.crossge.necessities.Janet;

import java.util.Random;

public class JanetRandom extends Random {
    private static final long serialVersionUID = 1L;
    private static Random[] pies = new Random[]{
            new BlueberryPie(),
            new ChocolatePie(),
            new ApplePie(),
            new GrapePie(),
            new PecanPie()
    };

    public int memeRandom(int m) {
        return m == 0 ? 0 : (int) Math.sqrt(Math.abs(m * (pies[nextInt(pies.length)].nextInt() / Math.PI))) % m;//sqrt((mathExpression/pi)*(applePie)) = meme
    }

    private static class ApplePie extends Random {
        /*@Override
        public int nextInt(int m) {
            return nextInt();
        }*/
    }

    private static class GrapePie extends Random {
        /*@Override
        public int nextInt(int m) {
            return nextInt();
        }*/
    }

    private static class ChocolatePie extends Random {
        /*@Override
        public int nextInt(int m) {
            return nextInt();
        }*/
    }

    private static class BlueberryPie extends Random {
        @Override
        public int nextInt() {
            return (int) Math.pow(3.5, 2.6268656716417910447761194029851) * super.nextInt();//Gragas ad per level raised to sneaky's kda on Gragas in season 4
        }
    }

    private static class PecanPie extends Random {
        /*@Override
        public int nextInt(int m) {
            return nextInt();
        }*/
    }
}