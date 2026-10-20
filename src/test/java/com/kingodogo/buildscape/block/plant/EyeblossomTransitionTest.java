package com.kingodogo.buildscape.block.plant;

import org.junit.Test;

public final class EyeblossomTransitionTest {

    @Test
    public void transitionsFollowDaylightAndWax() {
        check(false, false, false, false, "closed flower stays closed during day");
        check(true, false, true, false, "open flower stays open during night");
        check(false, false, true, true, "closed flower opens at night");
        check(true, false, false, true, "open flower closes during day");
        check(false, true, true, false, "waxed closed flower remains closed at night");
        check(true, true, false, false, "waxed open flower remains open during day");
    }

    private static void check(
            boolean currentlyOpen,
            boolean waxed,
            boolean night,
            boolean expected,
            String description) {
        boolean actual = EyeblossomTransitionEvents.shouldTransition(currentlyOpen, waxed, night);
        if (actual != expected) {
            throw new AssertionError(description + ": expected " + expected + ", got " + actual);
        }
    }
}
