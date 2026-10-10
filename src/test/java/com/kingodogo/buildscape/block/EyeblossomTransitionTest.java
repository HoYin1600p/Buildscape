package com.kingodogo.buildscape.block;

public final class EyeblossomTransitionTest {
    private static int checks;

    private EyeblossomTransitionTest() {
    }

    public static void main(String[] args) {
        check(false, false, false, false, "closed flower stays closed during day");
        check(true, false, true, false, "open flower stays open during night");
        check(false, false, true, true, "closed flower opens at night");
        check(true, false, false, true, "open flower closes during day");
        check(false, true, true, false, "waxed closed flower remains closed at night");
        check(true, true, false, false, "waxed open flower remains open during day");
        System.out.println("Eyeblossom transition tests passed: " + checks + " checks");
    }

    private static void check(
            boolean currentlyOpen,
            boolean waxed,
            boolean night,
            boolean expected,
            String description
    ) {
        boolean actual = EyeblossomTransitionHandler.shouldTransition(currentlyOpen, waxed, night);
        if (actual != expected) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual);
        }
        checks++;
    }
}
