package com.kingodogo.buildscape.test;

import com.kingodogo.buildscape.test.animations.AnimationTarget;
import net.minecraft.util.Mth;

public enum PetAnimation {
    IDLE {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            float idleScale = 1.0F - amount;
            t.rightArmX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 2.0F * amount * 0.5F;
            t.leftArmX = Mth.cos(swing * 0.6662F) * 2.0F * amount * 0.5F;
            t.rightLegX = Mth.cos(swing * 0.6662F) * 1.4F * amount;
            t.leftLegX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 1.4F * amount;

            if (!ground) {
                t.rightArmX = -1.0F; t.leftArmX = -1.0F;
                t.rightLegX = -0.5F; t.leftLegX = -0.5F;
            }

            if (idleScale > 0.5F) {
                float wave = Mth.sin(age * 0.05F);
                t.rightArmZ += wave * 0.1F; t.leftArmZ -= wave * 0.1F;
            }
        }
    },
    WALK {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightLegX = Mth.cos(swing * 0.6662F) * 1.4F * amount;
            t.leftLegX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 1.4F * amount;
            t.rightArmX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 2.0F * amount * 0.5F;
            t.leftArmX = Mth.cos(swing * 0.6662F) * 2.0F * amount * 0.5F;
        }
    },
    SPRINT {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightLegX = Mth.cos(swing * 0.6662F) * 1.4F * amount;
            t.leftLegX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 1.4F * amount;
            t.rightArmX = Mth.cos(swing * 0.6662F + (float)Math.PI) * 2.0F * amount * 0.8F;
            t.leftArmX = Mth.cos(swing * 0.6662F) * 2.0F * amount * 0.8F;
            t.headX = -0.2F * amount;
        }
    },
    SIT {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Base sit pose
            t.rightArmX = -0.628F; t.leftArmX = -0.628F;
            t.rightLegX = -1.413F; t.leftLegX = -1.413F;
            t.rightLegY = 0.314F; t.leftLegY = -0.314F;
            t.rootYPos = 10.0F;

            // ── Cute micro-animations while sitting ──────────────────────────
            // Gentle head tilt (left-right) — very slow, like a curious tilt
            t.headZ += Mth.sin(age * 0.025F) * 0.18F;
            // Tiny slow bob (up-down gaze) — as if it's watching you blink
            t.headX += Mth.sin(age * 0.04F) * 0.08F;
            // Subtle body sway so it doesn't look frozen
            t.bodyZ = Mth.sin(age * 0.02F) * 0.06F;
            // Arm fidget — one arm occasionally raises slightly
            t.rightArmX += Mth.sin(age * 0.035F + 1.0F) * 0.12F;
            t.leftArmX  += Mth.sin(age * 0.035F - 1.0F) * 0.08F;
        }
    },
    SLEEPING {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Lay flat on stomach
            t.rootX = (float)Math.PI / 2F;
            t.rootYPos = -10.0F;
            t.headX = -(float)Math.PI / 4F;
            t.rightArmX = -(float)Math.PI / 2F; t.leftArmX = -(float)Math.PI / 2F;
            t.rightLegX = -(float)Math.PI / 2F; t.leftLegX = -(float)Math.PI / 2F;

            // ── Sleepy micro-animations ──────────────────────────────────────
            // Slow breathing: chest gently rises and falls
            float breathe = Mth.sin(age * 0.035F); // ~2-second breath cycle
            t.bodyX += breathe * 0.08F;
            // Arms shift slightly with each breath
            t.rightArmX += breathe * 0.05F;
            t.leftArmX  += breathe * 0.05F;
            // Sleepy head drift side to side, very slowly
            t.headZ += Mth.sin(age * 0.018F) * 0.12F;
            // Very slight root rock, as if shifting weight in sleep
            t.rootZ = Mth.sin(age * 0.022F) * 0.04F;
        }
    },
    JUMPING {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Positive hop → lift upward (negative Y = up in model space)
            float hop = (Mth.sin(age * 0.4F) + 1.0F) * 0.5F; // always 0..1
            t.rootYPos = -hop * 8.0F; // Lifts UP, never sinks down
            t.rightArmX = -hop * 0.5F; t.leftArmX = -hop * 0.5F;
            t.rightLegX = hop * 0.2F; t.leftLegX = hop * 0.2F;
        }
    },
    HEAD_BANG {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headX = Mth.sin(age * 0.5F) * 0.8F;
            t.rightArmX = Mth.sin(age * 0.5F) * 0.5F - 0.5F;
            t.leftArmX = Mth.sin(age * 0.5F) * 0.5F - 0.5F;
        }
    },
    T_POSE {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightArmZ = (float)Math.PI / 2F; t.leftArmZ = -(float)Math.PI / 2F;
        }
    },
    PLAY_DEAD {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Lay on back — rootX = -PI/2. Lift model so it doesn't sink underground.
            t.rootX = -(float)Math.PI / 2F;
            t.rootYPos = -10.0F; // Lift model above ground
            t.rightArmX = -(float)Math.PI / 2F; t.leftArmX = -(float)Math.PI / 2F;
            t.rightLegX = -(float)Math.PI / 2F; t.leftLegX = -(float)Math.PI / 2F;
        }
    },
    CRAWL {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Crawling on all fours — rootX = PI/2. Lift to keep above ground.
            t.rootX = (float)Math.PI / 2F;
            t.rootYPos = -10.0F; // Lift above ground
            t.headX = -(float)Math.PI / 3F;
            t.rightArmY = Mth.sin(age * 0.2F) * 0.8F; t.leftArmY = -Mth.sin(age * 0.2F) * 0.8F;
            t.rightLegX = -(float)Math.PI / 2F + Mth.sin(age * 0.2F) * 0.5F;
            t.leftLegX = -(float)Math.PI / 2F - Mth.sin(age * 0.2F) * 0.5F;
        }
    },
    BOW {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            float phase = Math.max(0, Mth.sin(age * 0.1F)) * 0.8F;
            t.headX = phase;
            t.rightArmX = -phase; t.leftArmX = -phase;
        }
    },
    EXAMINE_FLOOR {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headX = 0.8F;
        }
    },
    STARGAZE {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headX = -1.0F;
        }
    },
    NOD {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headX = Mth.sin(age * 0.2F) * 0.5F;
        }
    },
    SHAKE_HEAD {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headY = Mth.sin(age * 0.2F) * 0.5F;
        }
    },
    BACKFLIP {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Continuously spinning backflip — always lifts, never sinks into the ground
            t.rootX = age * 0.3F;
            t.rightArmX = -0.5F; t.leftArmX = -0.5F;
            // Use abs of sin so the model always goes UP (negative Y = up), never down
            t.rootYPos = -Math.abs(Mth.sin(age * 0.3F)) * 14.0F;
        }
    },
    WAVE {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightArmZ = 2.0F; t.rightArmX = Mth.sin(age * 0.4F) * 0.5F;
        }
    },
    PROJECT_HOLOGRAM {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.headX = 0.2F; t.rightArmX = -1.5F; t.leftArmX = -1.5F;
        }
    },
    SWAP_MAINHAND {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightArmX = -Mth.sin(age * 0.3F) * 2.0F - 1.0F;
        }
    },
    SWAP_OFFHAND {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.leftArmX = -Mth.sin(age * 0.3F) * 2.0F - 1.0F;
        }
    },
    SLAP {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rightArmX = -1.5F + Mth.sin(age * 1.5F) * 1.5F;
        }
    },

    // ── NEW FUN POSES ────────────────────────────────────────────────────────

    /**
     * FLEX: Bodybuilder pose — both arms bent upward, head tilted back proudly.
     */
    FLEX {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Arms bent up like a flex — large negative X rotation to raise, then Z spreads them out
            t.rightArmX = -1.8F; t.leftArmX = -1.8F;
            t.rightArmZ = -0.6F; t.leftArmZ = 0.6F;
            // Subtle pump flex oscillation
            float pump = Mth.sin(age * 0.3F) * 0.1F;
            t.rightArmX += pump; t.leftArmX += pump;
            t.headX = -0.3F; // Looking up confidently
        }
    },

    /**
     * THINK: Right hand raised with chin-rub, slow body sway side to side.
     */
    THINK {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            float sway = Mth.sin(age * 0.06F) * 0.08F;
            t.bodyZ = sway;
            t.headZ = -sway * 0.5F;
            t.headX = 0.2F; // Thinking gaze
            // Right arm raised to the chin
            t.rightArmX = -1.2F;
            t.rightArmZ = -0.3F;
            // Left arm down, resting
            t.leftArmX = 0.1F;
            t.leftArmZ = 0.2F;
        }
    },

    /**
     * DANCE: Full-body party dance — alternating arm pumps, leg pops, and body bounce.
     */
    DANCE {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            float beat = age * 0.25F;
            float bounce = Mth.sin(beat) * 0.5F;
            // Body bounce up/down
            t.rootYPos = -Math.abs(bounce) * 4.0F;
            // Head bop
            t.headX = Mth.sin(beat) * 0.3F;
            // Arms alternate up/down pump
            t.rightArmX = -0.8F + Mth.sin(beat) * 0.8F;
            t.leftArmX = -0.8F - Mth.sin(beat) * 0.8F;
            t.rightArmZ = -0.3F + Mth.cos(beat) * 0.3F;
            t.leftArmZ = 0.3F - Mth.cos(beat) * 0.3F;
            // Leg pop
            t.rightLegX = Mth.sin(beat + (float)Math.PI) * 0.4F;
            t.leftLegX = Mth.sin(beat) * 0.4F;
            // Hip sway
            t.bodyY = Mth.sin(beat * 0.5F) * 0.2F;
        }
    },

    /**
     * POINT_UP: One arm pointing triumphantly straight up, head tilted back.
     */
    POINT_UP {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            // Right arm straight up — max negative X rotation
            t.rightArmX = -(float)Math.PI; // Points straight up
            t.rightArmZ = -0.1F; // Slight inward tilt for flair
            // Left arm casually to the side
            t.leftArmZ = 0.4F;
            t.leftArmX = 0.1F;
            // Head tilted back, gazing upward
            t.headX = -0.5F;
            // Subtle hold-still tremor for coolness
            float tremble = Mth.sin(age * 0.8F) * 0.03F;
            t.rightArmX += tremble;
        }
    },

    /**
     * SPIN: Entire body doing a slow triumphant Y-axis spin, arms spread wide.
     */
    SPIN {
        @Override
        public void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground) {
            t.rootY = age * 0.15F; // Continuous slow Y-axis spin
            // Arms spread wide outward
            t.rightArmZ = -(float)Math.PI / 2F;
            t.leftArmZ = (float)Math.PI / 2F;
            // Slightly raised for a dramatic spread-eagle look
            t.rightArmX = -0.3F; t.leftArmX = -0.3F;
            // Head follows the spin (counter-rotate slightly so head lags behind naturally)
            t.headY = -age * 0.05F;
        }
    };

    public abstract void applyMath(AnimationTarget t, float age, float swing, float amount, boolean ground);

    public static PetAnimation byId(int id) {
        if (id < 0 || id >= values().length) {
            return IDLE;
        }
        return values()[id];
    }
}
