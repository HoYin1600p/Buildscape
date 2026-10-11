package com.kingodogo.buildscape.adapter.v118x;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SignWoodTypesTest {
    @Test
    void bothSignWoodTypesAreRegisteredOnceDuringFactoryInitialization() throws IOException {
        // Plain JUnit does not apply runtime mixins. Inspect registration wiring
        // without initializing BlockFactory; the production-client scene verifies
        // registration itself, and MixinTargetCompatibilityTest checks the invoker.
        ClassNode factory = read("com/kingodogo/buildscape/adapter/v118x/BlockFactory");
        var initializer = factory.methods.stream().filter(method -> method.name.equals("<clinit>"))
                .findFirst().orElseThrow();
        for (String name : List.of("buildscape:mangrove", "buildscape:bamboo")) {
            int occurrences = 0;
            for (var instruction : initializer.instructions) {
                if (instruction instanceof LdcInsnNode constant && name.equals(constant.cst)) occurrences++;
            }
            assertEquals(1, occurrences, name + " must be initialized exactly once");
        }
        int registrations = 0;
        for (var instruction : initializer.instructions) {
            if (instruction instanceof MethodInsnNode call && call.name.equals("createRegistered")) registrations++;
        }
        assertEquals(2, registrations);

        ClassNode wood = read("com/kingodogo/buildscape/adapter/v118x/BlockFactory$CustomWoodType");
        var register = wood.methods.stream().filter(method -> method.name.equals("createRegistered"))
                .findFirst().orElseThrow();
        int invocations = 0;
        for (var instruction : register.instructions) {
            if (instruction instanceof MethodInsnNode call
                    && call.owner.equals("com/kingodogo/buildscape/mixin/WoodTypeV118xInvoker")
                    && call.name.equals("buildscape$register")) invocations++;
        }
        assertEquals(1, invocations, "Registration must use the remapped invoker");
    }

    private static ClassNode read(String name) throws IOException {
        try (InputStream stream = SignWoodTypesTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(stream, name);
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }
}
