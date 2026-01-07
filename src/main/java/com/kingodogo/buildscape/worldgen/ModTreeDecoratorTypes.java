package com.kingodogo.buildscape.worldgen;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTreeDecoratorTypes {

    public static final DeferredRegister<
            TreeDecoratorType<?>
            > TREE_DECORATOR_TYPES = DeferredRegister.create(
            Registry.TREE_DECORATOR_TYPE_REGISTRY,
            BuildScape.MODID
    );

    public static final RegistryObject<
            TreeDecoratorType<MangrovePropaguleDecorator>
            > MANGROVE_PROPAGULE = TREE_DECORATOR_TYPES.register(
            "mangrove_propagule",
            () -> new TreeDecoratorType<>(MangrovePropaguleDecorator.CODEC)
    );

    public static final RegistryObject<
            TreeDecoratorType<MangroveMossCarpetDecorator>
            > MANGROVE_MOSS_CARPET = TREE_DECORATOR_TYPES.register(
            "mangrove_moss_carpet",
            () -> new TreeDecoratorType<>(MangroveMossCarpetDecorator.CODEC)
    );

    public static final RegistryObject<
            TreeDecoratorType<MangroveRootDecorator>
            > MANGROVE_ROOT = TREE_DECORATOR_TYPES.register("mangrove_root", () ->
            new TreeDecoratorType<>(MangroveRootDecorator.CODEC)
    );

    public static final RegistryObject<
            TreeDecoratorType<MangroveLeaveVineDecorator>
            > MANGROVE_LEAVE_VINE = TREE_DECORATOR_TYPES.register(
            "mangrove_leave_vine",
            () -> new TreeDecoratorType<>(MangroveLeaveVineDecorator.CODEC)
    );
}
