package com.kingodogo.buildscape.block.vertical;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import net.minecraft.world.level.block.state.properties.Property;

public final class VerticalStairShapeProperty extends Property<VerticalStairShapeState> {
    private final Collection<VerticalStairShapeState> values;

    public VerticalStairShapeProperty() {
        super("front_top_shape", VerticalStairShapeState.class);
        this.values = Collections.unmodifiableList(Arrays.asList(VerticalStairShapeState.values()));
    }

    @Override
    public Collection<VerticalStairShapeState> getPossibleValues() {
        return this.values;
    }

    @Override
    public String getName(VerticalStairShapeState value) {
        return value.getSerializedName();
    }

    @Override
    public Optional<VerticalStairShapeState> getValue(String name) {
        return Optional.ofNullable(VerticalStairShapeState.byName(name));
    }
}
