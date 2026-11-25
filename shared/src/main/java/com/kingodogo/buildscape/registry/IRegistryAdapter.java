package com.kingodogo.buildscape.registry;

import java.util.Collection;
import java.util.function.Supplier;
public interface IRegistryAdapter {
    void init();
    <T> RegistrySupplier<T> registerBlock(String name, Supplier<T> blockSupplier);
    <T> RegistrySupplier<T> registerItem(String name, Supplier<T> itemSupplier);
    <T> RegistrySupplier<T> registerBlockEntity(String name, Supplier<T> blockEntitySupplier);
    <T> RegistrySupplier<T> registerEntity(String name, Supplier<T> entitySupplier);
    <T> RegistrySupplier<T> registerSound(String name, Supplier<T> soundSupplier);
    <T> RegistrySupplier<T> registerParticle(String name, Supplier<T> particleSupplier);
    <T> RegistrySupplier<T> registerCreativeTab(String name, Supplier<T> tabSupplier);
    <T> RegistrySupplier<T> registerRecipeSerializer(String name, Supplier<T> serializerSupplier);
    Collection<RegistrySupplier<?>> getRegisteredBlocks();
    Collection<RegistrySupplier<?>> getRegisteredItems();
    Collection<RegistrySupplier<?>> getRegisteredBlockEntities();
    Collection<RegistrySupplier<?>> getRegisteredEntities();
    Collection<RegistrySupplier<?>> getRegisteredParticles();
    Collection<RegistrySupplier<?>> getRegisteredRecipeSerializers();
}
