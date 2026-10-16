package com.kingodogo.buildscape.adapter.v121x;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Reads class files without asking the JVM to load or verify Minecraft client classes. */
class DedicatedServerAdapterSafetyTest {
    private static final String CLIENT = "net/minecraft/client/";
    private static final String ADAPTER = "com/kingodogo/buildscape/adapter/v121x/";

    @Test
    void sharedServerAdaptersDoNotForceClientVerification() throws IOException {
        List<String> problems = new ArrayList<>();
        for (String name : List.of("PlatformAdapterBase", "BlockFactory", "PacketFactory",
                "RecipeFactory", "WorldGenFactory", "MixinFactory")) {
            try (InputStream bytes = getClass().getClassLoader().getResourceAsStream(ADAPTER + name + ".class")) {
                assertNotNull(bytes, "Missing compiled v121x adapter: " + name);
                inspect(read(bytes), problems);
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void compiledNeoForgeServerAdaptersDoNotForceClientVerification() throws IOException {
        // Common's test runtime does not include loader classes. Inspect the loader's actual
        // output after :neoforge:1.21.1:compileJava/build, without adding loader dependencies.
        Path root = Path.of("").toAbsolutePath();
        while (root != null && !Files.isDirectory(root.resolve("neoforge/1.21.1"))) root = root.getParent();
        assertNotNull(root, "Cannot locate repository from the test working directory");
        Path output = root.resolve("neoforge/1.21.1/build/classes/java/main/com/kingodogo/buildscape");
        assumeTrue(Files.isRegularFile(output.resolve("platform/NeoForgePlatformAdapter.class")),
                "Build NeoForge 1.21.1 before running the loader bytecode audit");
        List<String> problems = new ArrayList<>();
        for (String name : List.of("platform/NeoForgePlatformAdapter", "registry/NeoForgeRegistryAdapter")) {
            Path file = output.resolve(name + ".class");
            assertTrue(Files.isRegularFile(file), "Missing compiled loader adapter: " + file);
            try (InputStream bytes = Files.newInputStream(file)) {
                inspect(read(bytes), problems);
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems));
    }

    @Test
    void guardAllowsSignaturesAndRejectsVerificationAndInitializationHazards() {
        ClassNode safe = new ClassNode();
        safe.name = ADAPTER + "Fixture";
        safe.fields.add(new FieldNode(0, "clientSignature", "L" + CLIENT + "Minecraft;", null, null));
        safe.methods.add(new MethodNode(0, "signature", "(L" + CLIENT + "Minecraft;)V", null, null));
        List<String> problems = new ArrayList<>();
        inspect(safe, problems);
        assertTrue(problems.isEmpty(), "Client descriptors alone must remain allowed");

        for (int opcode : List.of(Opcodes.NEW, Opcodes.CHECKCAST, Opcodes.INSTANCEOF)) {
            MethodNode method = new MethodNode(0, "hazard", "()V", null, null);
            method.instructions.add(new TypeInsnNode(opcode, CLIENT + "Minecraft"));
            assertRejected(method);
        }
        MethodNode invoke = new MethodNode(0, "staticCall", "()V", null, null);
        invoke.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, CLIENT + "Minecraft",
                "getInstance", "()L" + CLIENT + "Minecraft;", false));
        assertRejected(invoke);
        MethodNode caught = new MethodNode(0, "catchClient", "()V", null, null);
        caught.tryCatchBlocks.add(new TryCatchBlockNode(new LabelNode(), new LabelNode(), new LabelNode(),
                CLIENT + "ClientException"));
        assertRejected(caught);
        for (String name : List.of("<init>", "<clinit>")) {
            MethodNode initializer = new MethodNode(0, name, "()V", null, null);
            initializer.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, CLIENT + "Minecraft", "instance",
                    "L" + CLIENT + "Minecraft;"));
            assertRejected(initializer);
        }
        MethodNode lambda = new MethodNode(0, "<clinit>", "()V", null, null);
        lambda.instructions.add(new InvokeDynamicInsnNode("run", "()Ljava/lang/Runnable;",
                new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "()V", false),
                new Handle(Opcodes.H_INVOKESTATIC, CLIENT + "Minecraft", "getInstance",
                        "()L" + CLIENT + "Minecraft;", false)));
        assertRejected(lambda);
        ClassNode client = new ClassNode();
        client.name = ADAPTER + "client/Fixture";
        client.methods.add(invoke);
        problems.clear();
        inspect(client, problems);
        assertTrue(problems.isEmpty(), "Client helpers are intentionally excluded");
    }

    private static void assertRejected(MethodNode method) {
        ClassNode node = new ClassNode();
        node.name = ADAPTER + "Fixture";
        node.methods.add(method);
        List<String> problems = new ArrayList<>();
        inspect(node, problems);
        assertFalse(problems.isEmpty(), "Guard missed " + method.name);
    }

    private static ClassNode read(InputStream bytes) throws IOException {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return node;
    }

    private static boolean client(String value) {
        return value != null && (value.startsWith(CLIENT) || value.contains("L" + CLIENT));
    }

    private static boolean clientConstant(Object value) {
        if (value instanceof Type type) return client(type.getDescriptor());
        if (value instanceof Handle handle) return client(handle.getOwner());
        return false;
    }

    private static void inspect(ClassNode node, List<String> problems) {
        if (node.name.contains("/client/")) return;
        for (FieldNode field : node.fields) {
            if (clientConstant(field.value)) problems.add(node.name + ": client field initializer " + field.name);
        }
        for (MethodNode method : node.methods) {
            String context = node.name + "#" + method.name + method.desc;
            boolean initializer = method.name.equals("<clinit>") || method.name.equals("<init>");
            for (TryCatchBlockNode handler : method.tryCatchBlocks) {
                if (client(handler.type)) problems.add(context + ": client catch " + handler.type);
            }
            for (AbstractInsnNode instruction : method.instructions) {
                if (instruction instanceof TypeInsnNode type && client(type.desc)) {
                    problems.add(context + ": client type instruction " + type.getOpcode() + " " + type.desc);
                } else if (instruction instanceof MethodInsnNode call && client(call.owner)
                        && (call.getOpcode() == Opcodes.INVOKESTATIC || initializer)) {
                    problems.add(context + ": client invocation " + call.owner + "." + call.name);
                } else if (instruction instanceof FieldInsnNode field && initializer
                        && (client(field.owner) || client(field.desc))) {
                    problems.add(context + ": client initializer field " + field.owner + "." + field.name);
                } else if (instruction instanceof LdcInsnNode constant && clientConstant(constant.cst)) {
                    problems.add(context + ": client class constant");
                } else if (instruction instanceof InvokeDynamicInsnNode dynamic) {
                    boolean unsafe = clientConstant(dynamic.bsm);
                    for (Object argument : dynamic.bsmArgs) {
                        // A helper's descriptor is allowed. A handle owned by a Minecraft client
                        // class forces resolution; client method types in an initializer do too.
                        unsafe |= argument instanceof Handle handle && client(handle.getOwner());
                        unsafe |= initializer && clientConstant(argument);
                    }
                    if (unsafe) problems.add(context + ": client bootstrap/lambda");
                }
            }
        }
    }
}
