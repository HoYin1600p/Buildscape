package com.kingodogo.buildscape.client.renderer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Data holder for mob variant states parsed from spawn egg names.
 */
public class MobState {

    public final Set<String> parsedStates = new HashSet<>();

    public boolean spin;
    public boolean upsideDown;
    public boolean baby;
    public boolean angry;
    public boolean sitting;
    public boolean charged;
    public boolean sheared;
    public boolean saddled;
    public boolean tamed;
    public boolean powered;
    public boolean invisible;
    public boolean glowing;
    public boolean fire;
    public boolean frozen;

    public MobState() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MobState mobState = (MobState) o;
        return spin == mobState.spin &&
                upsideDown == mobState.upsideDown &&
                baby == mobState.baby &&
                angry == mobState.angry &&
                sitting == mobState.sitting &&
                charged == mobState.charged &&
                sheared == mobState.sheared &&
                saddled == mobState.saddled &&
                tamed == mobState.tamed &&
                powered == mobState.powered &&
                invisible == mobState.invisible &&
                glowing == mobState.glowing &&
                fire == mobState.fire &&
                frozen == mobState.frozen &&
                Objects.equals(parsedStates, mobState.parsedStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parsedStates, spin, upsideDown, baby, angry, sitting, charged, sheared, saddled, tamed, powered, invisible, glowing, fire, frozen);
    }

    @Override
    public String toString() {
        return "MobState{" +
                "parsedStates=" + parsedStates +
                ", spin=" + spin +
                ", upsideDown=" + upsideDown +
                ", baby=" + baby +
                ", angry=" + angry +
                ", sitting=" + sitting +
                ", charged=" + charged +
                ", sheared=" + sheared +
                ", saddled=" + saddled +
                ", tamed=" + tamed +
                ", powered=" + powered +
                ", invisible=" + invisible +
                ", glowing=" + glowing +
                ", fire=" + fire +
                ", frozen=" + frozen +
                '}';
    }
}
