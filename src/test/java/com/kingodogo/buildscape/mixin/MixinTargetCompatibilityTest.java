package com.kingodogo.buildscape.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks bytecode without loading Minecraft classes or starting its registries/client. */
class MixinTargetCompatibilityTest {
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String PSEUDO = "Lorg/spongepowered/asm/mixin/Pseudo;";
    private static final String COERCE = "Lorg/spongepowered/asm/mixin/injection/Coerce;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final String CALLBACK = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    private static final String RETURN_CALLBACK = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";
    private static final Set<String> INJECTIONS = Set.of("Inject", "Redirect", "ModifyArg", "ModifyVariable");
    // Forge classes are absent from this Minecraft-only bytecode classpath; Embeddium is optional.
    private static final Set<String> EXPECTED_MISSING_CLASSES = Set.of(
            "com/kingodogo/buildscape/mixin/BuildscapeForgeRegistryMixin -> net.minecraftforge.registries.ForgeRegistry",
            "com/kingodogo/buildscape/mixin/EmbeddiumPipeSpillMixin -> me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer",
            "com/kingodogo/buildscape/mixin/biometint/EmbeddiumBiomeColorMixin -> me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache",
            "com/kingodogo/buildscape/mixin/compat/QuarkBeaconRedirectionMixin -> vazkii.quark.content.tools.module.BeaconRedirectionModule");
    // Only these selectors may miss: 26.2's parallel-model lifecycle and the production SRG alias
    // alongside ItemStack's Mojang-named getMaxStackSize. Invalid callbacks/@At sites still fail.
    private static final Set<String> EXPECTED_MISSING_METHODS = Set.of(
            "com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin -> net/minecraft/client/resources/model/ModelBakery#buildscape$parseModelsInParallel#processLoading",
            "com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin -> net/minecraft/client/resources/model/ModelBakery#buildscape$releaseParsedModels#processLoading");

    @Test
    void allConfiguredTargetsMatchMinecraft1182() throws IOException {
        String prop = System.getProperty("buildscape.mixinConfig");
        Path configPath = prop != null ? Path.of(prop) : Path.of("src/main/resources/buildscape.mixins.json");
        JsonObject config;
        try (var reader = Files.newBufferedReader(configPath)) {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        }
        Audit audit = new Audit();
        int defaultRequire = config.has("injectors") && config.getAsJsonObject("injectors").has("defaultRequire")
                ? config.getAsJsonObject("injectors").get("defaultRequire").getAsInt()
                : 1;
        int count = 0;
        for (String side : List.of("mixins", "client", "server")) {
            JsonArray names = config.getAsJsonArray(side);
            if (names == null) {
                continue;
            }
            for (var name : names) {
                String className = config.get("package").getAsString() + "." + name.getAsString();
                try {
                    ClassNode mixin = audit.read(className.replace('.', '/'));
                    if (mixin == null) {
                        audit.problem(false, className + ": configured mixin class is missing");
                    } else {
                        audit.mixin(mixin, defaultRequire);
                    }
                } catch (IOException | IllegalArgumentException exception) {
                    audit.problem(false, className + ": cannot inspect bytecode: " + exception);
                }
                count++;
            }
        }
        audit.warnings.forEach(warning -> System.err.println("Optional mixin target warning: " + warning));
        assertTrue(count > 0, "No mixins were audited");
        assertTrue(audit.failures.isEmpty(), "Minecraft 1.18.2 mixin mismatches (" + audit.failures.size()
                + "):\n" + String.join("\n", audit.failures));
    }

    @Test
    void auditRejectsUnexpectedOptionalMissesAndInvalidCallbacks() throws IOException {
        Audit audit = new Audit();
        ClassNode target = new ClassNode();
        target.name = "fixture/Target";
        target.methods.add(new MethodNode(0, "load", "(Ljava/lang/String;I)V", null, null));
        audit.classes.put(target.name, target);
        ClassNode mixin = new ClassNode();
        mixin.name = "fixture/Mixin";
        mixin.invisibleAnnotations = List.of(annotation(MIXIN, "targets", List.of("fixture.Target")));
        MethodNode badCallback = new MethodNode(0, "badCallback", "(Ljava/lang/Object;I" + CALLBACK + ")V", null, null);
        badCallback.visibleAnnotations = List.of(annotation(injectionDescriptor("Inject"), "method", List.of("load")));
        mixin.methods.add(badCallback);
        MethodNode missing = new MethodNode(0, "missing", "()V", null, null);
        missing.visibleAnnotations = List.of(annotation(SHADOW));
        mixin.methods.add(missing);
        MethodNode optional = new MethodNode(0, "optional", "(" + CALLBACK + ")V", null, null);
        optional.visibleAnnotations = List
                .of(annotation(injectionDescriptor("Inject"), "method", List.of("absent"), "require", 0));
        mixin.methods.add(optional);
        audit.mixin(mixin, 1);
        assertEquals(3, audit.failures.size(), String.join("\n", audit.failures));
        assertEquals(0, audit.warnings.size());
        badCallback.invisibleParameterAnnotations = parameterAnnotations(annotation(COERCE));
        Audit coerced = new Audit();
        coerced.classes.put(target.name, target);
        coerced.mixin(mixin, 1);
        assertEquals(2, coerced.failures.size(),
                "@Coerce Object must accept reference arguments; optional misses still fail");
    }

    @Test
    void auditAllowsOnlyListedOptionalMissingTargets() throws IOException {
        Audit audit = new Audit();
        ClassNode target = new ClassNode();
        target.name = "net/minecraft/client/resources/model/ModelBakery";
        audit.classes.put(target.name, target);
        ClassNode mixin = new ClassNode();
        mixin.name = "com/kingodogo/buildscape/mixin/BuildscapeModelBakeryMixin";
        mixin.invisibleAnnotations = List.of(annotation(MIXIN, "targets", List.of(target.name)));
        MethodNode allowed = new MethodNode(0, "buildscape$parseModelsInParallel", "(" + CALLBACK + ")V", null, null);
        allowed.visibleAnnotations = List
                .of(annotation(injectionDescriptor("Inject"), "method", List.of("processLoading"), "require", 0));
        mixin.methods.add(allowed);
        audit.mixin(mixin, 1);
        assertEquals(0, audit.failures.size());
        assertEquals(1, audit.warnings.size());

        // Even an allowlisted selector fails when required, or when present with a bad callback.
        allowed.visibleAnnotations = List
                .of(annotation(injectionDescriptor("Inject"), "method", List.of("processLoading"), "require", 1));
        audit.mixin(mixin, 1);
        assertEquals(1, audit.failures.size());
        target.methods.add(new MethodNode(0, "processLoading", "()I", null, null));
        allowed.visibleAnnotations = List
                .of(annotation(injectionDescriptor("Inject"), "method", List.of("processLoading"), "require", 0));
        audit.mixin(mixin, 1);
        assertEquals(2, audit.failures.size());
        assertEquals(1, audit.warnings.size());
    }

    @Test
    void auditRejectsConstructorHeadInjection() throws IOException {
        Audit audit = new Audit();
        ClassNode target = new ClassNode();
        target.name = "fixture/Target";
        target.methods.add(new MethodNode(0, "<init>", "()V", null, null));
        audit.classes.put(target.name, target);
        ClassNode mixin = new ClassNode();
        mixin.name = "fixture/Mixin";
        mixin.invisibleAnnotations = List.of(annotation(MIXIN, "targets", List.of("fixture.Target")));
        MethodNode badConstructorInject = new MethodNode(0, "onInit", "(" + CALLBACK + ")V", null, null);
        badConstructorInject.visibleAnnotations = List.of(annotation(injectionDescriptor("Inject"),
                "method", List.of("<init>"),
                "at", annotation("Lorg/spongepowered/asm/mixin/injection/At;", "value", "HEAD")));
        mixin.methods.add(badConstructorInject);
        audit.mixin(mixin, 1);
        assertEquals(1, audit.failures.size());
        assertTrue(audit.failures.get(0).contains("@Inject cannot target constructor <init> at @At(\"HEAD\")"));
    }

    @SuppressWarnings("unchecked")
    private static List<AnnotationNode>[] parameterAnnotations(AnnotationNode annotation) {
        List<AnnotationNode>[] result = new List[3];
        result[0] = List.of(annotation);
        return result;
    }

    private static AnnotationNode annotation(String descriptor, Object... values) {
        AnnotationNode node = new AnnotationNode(descriptor);
        node.values = new ArrayList<>(Arrays.asList(values));
        return node;
    }

    private static String injectionDescriptor(String name) {
        return "Lorg/spongepowered/asm/mixin/injection/" + name + ";";
    }

    private static Object value(AnnotationNode annotation, String key, Object fallback) {
        if (annotation != null && annotation.values != null) {
            for (int i = 0; i < annotation.values.size(); i += 2) {
                if (key.equals(annotation.values.get(i))) {
                    return annotation.values.get(i + 1);
                }
            }
        }
        return fallback;
    }

    private static List<AnnotationNode> annotations(List<AnnotationNode> visible, List<AnnotationNode> invisible) {
        List<AnnotationNode> result = new ArrayList<>();
        if (visible != null) {
            result.addAll(visible);
        }
        if (invisible != null) {
            result.addAll(invisible);
        }
        return result;
    }

    private static AnnotationNode find(List<AnnotationNode> annotations, String descriptor) {
        return annotations.stream().filter(annotation -> descriptor.equals(annotation.desc)).findFirst().orElse(null);
    }

    private static List<String> strings(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return value instanceof String string && !string.isEmpty() ? List.of(string) : List.of();
    }

    private static final class Audit {
        final Map<String, ClassNode> classes = new HashMap<>();
        final List<String> failures = new ArrayList<>();
        final List<String> warnings = new ArrayList<>();

        ClassNode read(String name) throws IOException {
            if (classes.containsKey(name)) {
                return classes.get(name);
            }
            try (InputStream stream = getClass().getClassLoader().getResourceAsStream(name + ".class")) {
                ClassNode node = null;
                if (stream != null) {
                    node = new ClassNode();
                    new ClassReader(stream).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }
                classes.put(name, node);
                return node;
            }
        }

        void problem(boolean optional, String message) {
            // require=0 controls runtime applicability, never permission to silently lose behaviour.
            failures.add(message);
        }

        void missingTarget(boolean optional, boolean expected, String message) {
            (optional && expected ? warnings : failures).add(message);
        }

        void mixin(ClassNode mixin, int defaultRequire) throws IOException {
            List<AnnotationNode> classAnnotations = annotations(mixin.visibleAnnotations, mixin.invisibleAnnotations);
            AnnotationNode annotation = find(classAnnotations, MIXIN);
            if (annotation == null) {
                problem(false, mixin.name + ": missing @Mixin");
                return;
            }
            List<String> targets = new ArrayList<>(strings(value(annotation, "targets", List.of())));
            Object literals = value(annotation, "value", List.of());
            if (literals instanceof List<?> list) {
                for (Object literal : list) {
                    targets.add(((Type) literal).getInternalName());
                }
            }
            if (targets.isEmpty()) {
                problem(false, mixin.name + ": @Mixin has no targets");
            }
            for (String name : targets) {
                ClassNode target = read(name.replace('.', '/'));
                if (target == null) {
                    String key = mixin.name + " -> " + name;
                    missingTarget(find(classAnnotations, PSEUDO) != null, EXPECTED_MISSING_CLASSES.contains(key),
                            key + ": target class is missing");
                    continue;
                }
                for (FieldNode field : mixin.fields) {
                    AnnotationNode shadow = find(annotations(field.visibleAnnotations, field.invisibleAnnotations),
                            SHADOW);
                    if (shadow != null) {
                        List<String> names = shadowNames(field.name, shadow);
                        if (!hasField(target, names, field.desc, isStatic(field.access))) {
                            problem(false, context(mixin, target) + "@Shadow field " + names + ":" + field.desc);
                        }
                    }
                }
                for (MethodNode method : mixin.methods) {
                    for (AnnotationNode member : annotations(method.visibleAnnotations, method.invisibleAnnotations)) {
                        String context = context(mixin, target) + method.name + method.desc + " ";
                        if (SHADOW.equals(member.desc)) {
                            if (!hasMethod(target, shadowNames(method.name, member), method.desc,
                                    isStatic(method.access))) {
                                problem(false, context + "@Shadow method is missing");
                            }
                        } else if (ACCESSOR.equals(member.desc)) {
                            Type[] args = Type.getArgumentTypes(method.desc);
                            Type result = Type.getReturnType(method.desc);
                            String descriptor = result.equals(Type.VOID_TYPE) && args.length == 1
                                    ? args[0].getDescriptor()
                                    : result.getDescriptor();
                            String nameValue = (String) value(member, "value",
                                    inferName(method.name, "get", "set", "is"));
                            if (!hasField(target, List.of(nameValue), descriptor, isStatic(method.access))) {
                                problem(false, context + "@Accessor " + nameValue + ":" + descriptor + " is missing");
                            }
                        } else if (INVOKER.equals(member.desc)) {
                            String nameValue = (String) value(member, "value",
                                    inferName(method.name, "call", "invoke"));
                            if (!hasMethod(target, List.of(nameValue), method.desc, isStatic(method.access))) {
                                problem(false, context + "@Invoker " + nameValue + method.desc + " is missing");
                            }
                        } else {
                            for (String kind : INJECTIONS) {
                                if (injectionDescriptor(kind).equals(member.desc)) {
                                    injection(mixin, target, method, member, kind, defaultRequire);
                                }
                            }
                        }
                    }
                }
            }
        }

        void injection(ClassNode mixin, ClassNode target, MethodNode handler, AnnotationNode annotation,
                String kind, int defaultRequire) throws IOException {
            int require = (Integer) value(annotation, "require", -1);
            boolean optional = (require < 0 ? defaultRequire : require) == 0;
            String context = context(mixin, target) + "@" + kind + " " + handler.name + handler.desc + " ";
            List<MethodNode> matches = new ArrayList<>();
            for (String selector : strings(value(annotation, "method", List.of()))) {
                List<MethodNode> selected = target.methods.stream().filter(method -> selects(selector, method))
                        .toList();
                if (selected.isEmpty()) {
                    String key = mixin.name + " -> " + target.name + "#" + handler.name + "#" + selector;
                    missingTarget(optional, EXPECTED_MISSING_METHODS.contains(key),
                            context + "target method " + selector + " is missing");
                }
                matches.addAll(selected);
            }
            for (MethodNode method : matches) {
                if (kind.equals("Inject") && !callbackMatches(handler, method)) {
                    problem(optional, context + "invalid callback descriptor for " + method.name + method.desc);
                }
                if (kind.equals("ModifyVariable") && !variableMatches(handler, method, annotation)) {
                    problem(optional,
                            context + "invalid modified variable descriptor for " + method.name + method.desc);
                }
                Object atValue = value(annotation, "at", List.of());
                List<?> points = atValue instanceof List<?> list ? list : List.of(atValue);
                for (Object point : points) {
                    if (point instanceof AnnotationNode at) {
                        inspectAt(context, method, handler, at, kind, annotation, optional);
                    }
                }
            }
        }

        void inspectAt(String context, MethodNode method, MethodNode handler, AnnotationNode at, String kind,
                AnnotationNode injection, boolean optional) throws IOException {
            if (kind.equals("Inject") && method.name.equals("<init>")) {
                String atVal = (String) value(at, "value", "");
                if ("HEAD".equals(atVal)) {
                    problem(optional, context + "@Inject cannot target constructor <init> at @At(\"HEAD\")");
                }
            }
            String selector = (String) value(at, "target", "");
            if (selector.isEmpty() || !selector.startsWith("L")) {
                return;
            }
            int ownerEnd = selector.indexOf(';');
            if (ownerEnd < 0) {
                return;
            }
            String owner = selector.substring(1, ownerEnd);
            String member = selector.substring(ownerEnd + 1);
            ClassNode target = read(owner);
            int descStart = member.indexOf('(');
            if (descStart >= 0) {
                String name = member.substring(0, descStart);
                String descriptor = member.substring(descStart);
                if (target == null || !hasMethod(target, List.of(name), descriptor, null)) {
                    problem(optional, context + "@At method " + selector + " is missing");
                }
                List<MethodInsnNode> sites = new ArrayList<>();
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof MethodInsnNode invoke && owner.equals(invoke.owner)
                            && name.equals(invoke.name) && descriptor.equals(invoke.desc)) {
                        sites.add(invoke);
                    }
                }
                if (sites.isEmpty()) {
                    problem(optional,
                            context + "@At invocation " + selector + " is absent from " + method.name + method.desc);
                }
                if (kind.equals("Redirect")) {
                    for (MethodInsnNode site : sites) {
                        String expected = site.getOpcode() == Opcodes.INVOKESTATIC
                                ? site.desc
                                : "(L" + site.owner + ";" + site.desc.substring(1);
                        if (!handlerDescriptorMatches(handler, expected, method.desc)) {
                            problem(optional, context + "redirect descriptor must match " + expected);
                        }
                    }
                } else if (kind.equals("ModifyArg")) {
                    Type[] arguments = Type.getArgumentTypes(descriptor);
                    int index = (Integer) value(injection, "index", -1);
                    Type returned = Type.getReturnType(handler.desc);
                    if (index >= arguments.length || index < -1 || (index >= 0 && !returned.equals(arguments[index]))
                            || (index == -1 && Arrays.stream(arguments).noneMatch(returned::equals))) {
                        problem(optional, context + "modified argument type does not match " + selector);
                    }
                }
            } else if (member.contains(":")) {
                int colon = member.indexOf(':');
                String name = member.substring(0, colon);
                String descriptor = member.substring(colon + 1);
                if (target == null || !hasField(target, List.of(name), descriptor, null)) {
                    problem(optional, context + "@At field " + selector + " is missing");
                }
                boolean found = false;
                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction instanceof FieldInsnNode field && owner.equals(field.owner)
                            && name.equals(field.name) && descriptor.equals(field.desc)) {
                        found = true;
                    }
                }
                if (!found) {
                    problem(optional, context + "@At field instruction " + selector + " is absent");
                }
            }
        }

        boolean callbackMatches(MethodNode handler, MethodNode target) throws IOException {
            Type[] arguments = Type.getArgumentTypes(handler.desc);
            Type[] expected = Type.getArgumentTypes(target.desc);
            String callback = Type.getReturnType(target.desc).equals(Type.VOID_TYPE) ? CALLBACK : RETURN_CALLBACK;
            int callbackIndex = -1;
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].getDescriptor().equals(callback)) {
                    callbackIndex = i;
                    break;
                }
            }
            if (callbackIndex < 0 || !Type.getReturnType(handler.desc).equals(Type.VOID_TYPE)) {
                return false;
            }
            if (callbackIndex != 0 && callbackIndex != expected.length) {
                return false;
            }
            for (int i = 0; i < callbackIndex; i++) {
                if (!parameterMatches(handler, i, arguments[i], expected[i])) {
                    return false;
                }
            }
            return isStatic(handler.access) == isStatic(target.access);
        }

        // A @Redirect handler may also capture the target method's arguments after the redirected
        // call's own arguments (Mixin argument capture); those must match the target's leading parameters.
        boolean handlerDescriptorMatches(MethodNode handler, String expectedDescriptor, String targetDescriptor)
                throws IOException {
            Type[] actual = Type.getArgumentTypes(handler.desc);
            Type[] expected = Type.getArgumentTypes(expectedDescriptor);
            Type[] captured = Type.getArgumentTypes(targetDescriptor);
            if (actual.length < expected.length || actual.length > expected.length + captured.length
                    || !Type.getReturnType(handler.desc).equals(Type.getReturnType(expectedDescriptor))) {
                return false;
            }
            for (int i = 0; i < expected.length; i++) {
                if (!parameterMatches(handler, i, actual[i], expected[i])) {
                    return false;
                }
            }
            for (int i = expected.length; i < actual.length; i++) {
                if (!actual[i].equals(captured[i - expected.length])) {
                    return false;
                }
            }
            return true;
        }

        boolean variableMatches(MethodNode handler, MethodNode target, AnnotationNode annotation) throws IOException {
            Type[] actual = Type.getArgumentTypes(handler.desc);
            Type returned = Type.getReturnType(handler.desc);
            Type[] expected = Type.getArgumentTypes(target.desc);
            if (actual.length == 0 || !actual[0].equals(returned) || actual.length - 1 > expected.length) {
                return false;
            }
            if (Boolean.TRUE.equals(value(annotation, "argsOnly", false))) {
                int ordinal = (Integer) value(annotation, "ordinal", -1);
                long matchingArguments = Arrays.stream(expected).filter(returned::equals).count();
                if (matchingArguments == 0 || ordinal >= matchingArguments) {
                    return false;
                }
            }
            // Variable handlers may capture a prefix of the target's arguments after the value.
            for (int i = 1; i < actual.length; i++) {
                if (!parameterMatches(handler, i, actual[i], expected[i - 1])) {
                    return false;
                }
            }
            return isStatic(handler.access) == isStatic(target.access);
        }

        boolean parameterMatches(MethodNode handler, int index, Type actual, Type expected) throws IOException {
            if (actual.equals(expected)) {
                return true;
            }
            List<AnnotationNode> visible = handler.visibleParameterAnnotations == null
                    || index >= handler.visibleParameterAnnotations.length
                            ? null
                            : handler.visibleParameterAnnotations[index];
            List<AnnotationNode> invisible = handler.invisibleParameterAnnotations == null
                    || index >= handler.invisibleParameterAnnotations.length
                            ? null
                            : handler.invisibleParameterAnnotations[index];
            return find(annotations(visible, invisible), COERCE) != null && actual.getSort() == Type.OBJECT
                    && (expected.getSort() == Type.OBJECT || expected.getSort() == Type.ARRAY)
                    && (actual.getInternalName().equals("java/lang/Object")
                            || (expected.getSort() == Type.OBJECT && ancestor(expected.getInternalName(),
                                    actual.getInternalName(), new HashSet<>())));
        }

        boolean ancestor(String child, String parent, Set<String> seen) throws IOException {
            if (child.equals(parent)) {
                return true;
            }
            if (!seen.add(child)) {
                return false;
            }
            ClassNode node = read(child);
            if (node == null) {
                return false;
            }
            if (node.superName != null && ancestor(node.superName, parent, seen)) {
                return true;
            }
            for (String face : node.interfaces) {
                if (ancestor(face, parent, seen)) {
                    return true;
                }
            }
            return false;
        }

        boolean hasField(ClassNode target, List<String> names, String descriptor, Boolean statik) throws IOException {
            for (FieldNode field : target.fields) {
                if (names.contains(field.name) && descriptor.equals(field.desc)
                        && (statik == null || statik == isStatic(field.access))) {
                    return true;
                }
            }
            ClassNode parent = target.superName == null ? null : read(target.superName);
            return parent != null && hasField(parent, names, descriptor, statik);
        }

        boolean hasMethod(ClassNode target, List<String> names, String descriptor, Boolean statik) throws IOException {
            for (MethodNode method : target.methods) {
                if (names.contains(method.name) && descriptor.equals(method.desc)
                        && (statik == null || statik == isStatic(method.access))) {
                    return true;
                }
            }
            ClassNode parent = target.superName == null ? null : read(target.superName);
            if (parent != null && hasMethod(parent, names, descriptor, statik)) {
                return true;
            }
            for (String face : target.interfaces) {
                ClassNode node = read(face);
                if (node != null && hasMethod(node, names, descriptor, statik)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean selects(String selector, MethodNode method) {
            int descriptorStart = selector.indexOf('(');
            String name = descriptorStart < 0 ? selector : selector.substring(0, descriptorStart);
            int ownerEnd = name.lastIndexOf(';');
            if (ownerEnd >= 0) {
                name = name.substring(ownerEnd + 1);
            }
            boolean nameMatches = name.endsWith("*")
                    ? method.name.startsWith(name.substring(0, name.length() - 1))
                    : method.name.equals(name);
            return nameMatches && (descriptorStart < 0 || method.desc.equals(selector.substring(descriptorStart)));
        }

        private static List<String> shadowNames(String name, AnnotationNode annotation) {
            String prefix = (String) value(annotation, "prefix", "shadow$");
            List<String> names = new ArrayList<>();
            names.add(name.startsWith(prefix) ? name.substring(prefix.length()) : name);
            names.addAll(strings(value(annotation, "aliases", List.of())));
            return names;
        }

        private static String inferName(String name, String... prefixes) {
            for (String prefix : prefixes) {
                if (name.startsWith(prefix) && name.length() > prefix.length()) {
                    String tail = name.substring(prefix.length());
                    return Character.toLowerCase(tail.charAt(0)) + tail.substring(1);
                }
            }
            return name;
        }

        private static boolean isStatic(int access) {
            return (access & Opcodes.ACC_STATIC) != 0;
        }
        private static String context(ClassNode mixin, ClassNode target) {
            return mixin.name + " -> " + target.name + ": ";
        }
    }
}
