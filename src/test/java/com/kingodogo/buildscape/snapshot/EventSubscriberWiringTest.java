package com.kingodogo.buildscape.snapshot;

import com.kingodogo.buildscape.BuildScape;
import org.junit.Test;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Pins every {@code @Mod.EventBusSubscriber} class and its {@code @SubscribeEvent} methods (bus, dists, event type,
 * priority) so a move or rename cannot silently drop a handler. The compiled classes are read with ASM, because
 * loading the annotation type through reflection needs a running mod loader. Update with {@code SNAPSHOT_UPDATE=1}.
 */
public class EventSubscriberWiringTest {

    private static final String SUBSCRIBER = "Lnet/minecraftforge/fml/common/Mod$EventBusSubscriber;";
    private static final String SUBSCRIBE = "Lnet/minecraftforge/eventbus/api/SubscribeEvent;";

    private static final class Handler {
        String name;
        String eventType = "";
        int parameterCount;
        boolean isPublic;
        boolean isStatic;
        String priority = "NORMAL";
        boolean receiveCanceled;
    }

    private static final class Subscriber {
        String name;
        String bus = "FORGE";
        List<String> dists = new ArrayList<>(List.of("CLIENT", "DEDICATED_SERVER"));
        final List<Handler> handlers = new ArrayList<>();

        String distText() {
            return dists.stream().sorted().collect(Collectors.joining(","));
        }
    }

    private static List<Subscriber> subscribers() throws Exception {
        Path root = Path.of(BuildScape.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        List<Path> classFiles;
        try (Stream<Path> files = Files.walk(root)) {
            classFiles = files.filter(p -> p.toString().endsWith(".class")).sorted().collect(Collectors.toList());
        }
        List<Subscriber> result = new ArrayList<>();
        for (Path file : classFiles) {
            Subscriber subscriber = read(file);
            if (subscriber != null) {
                result.add(subscriber);
            }
        }
        result.sort((a, b) -> a.name.compareTo(b.name));
        return result;
    }

    private static Subscriber read(Path file) throws IOException {
        Subscriber subscriber = new Subscriber();
        boolean[] annotated = {false};
        try (InputStream stream = Files.newInputStream(file)) {
            new ClassReader(stream).accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName,
                                  String[] interfaces) {
                    subscriber.name = name.replace('/', '.');
                }

                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    if (!SUBSCRIBER.equals(descriptor)) {
                        return null;
                    }
                    annotated[0] = true;
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visitEnum(String name, String descriptor, String value) {
                            if ("bus".equals(name)) {
                                subscriber.bus = value;
                            }
                        }

                        @Override
                        public AnnotationVisitor visitArray(String name) {
                            if (!"value".equals(name)) {
                                return null;
                            }
                            subscriber.dists = new ArrayList<>();
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitEnum(String name, String descriptor, String value) {
                                    subscriber.dists.add(value);
                                }
                            };
                        }
                    };
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
                                                 String[] exceptions) {
                    Handler handler = new Handler();
                    handler.name = name;
                    handler.isPublic = (access & Opcodes.ACC_PUBLIC) != 0;
                    handler.isStatic = (access & Opcodes.ACC_STATIC) != 0;
                    Type[] arguments = Type.getArgumentTypes(descriptor);
                    handler.parameterCount = arguments.length;
                    if (arguments.length > 0) {
                        handler.eventType = arguments[0].getClassName();
                    }
                    return new MethodVisitor(Opcodes.ASM9) {
                        @Override
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (!SUBSCRIBE.equals(descriptor)) {
                                return null;
                            }
                            subscriber.handlers.add(handler);
                            return new AnnotationVisitor(Opcodes.ASM9) {
                                @Override
                                public void visitEnum(String name, String descriptor, String value) {
                                    if ("priority".equals(name)) {
                                        handler.priority = value;
                                    }
                                }

                                @Override
                                public void visit(String name, Object value) {
                                    if ("receiveCanceled".equals(name)) {
                                        handler.receiveCanceled = Boolean.TRUE.equals(value);
                                    }
                                }
                            };
                        }
                    };
                }
            }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        if (!annotated[0]) {
            return null;
        }
        subscriber.handlers.sort((a, b) -> a.name.compareTo(b.name));
        return subscriber;
    }

    @Test
    public void subscriberWiringMatchesSnapshot() throws Exception {
        List<String> lines = new ArrayList<>();
        for (Subscriber subscriber : subscribers()) {
            lines.add(subscriber.name + " bus=" + subscriber.bus + " dist=" + subscriber.distText());
            for (Handler handler : subscriber.handlers) {
                lines.add("  " + handler.name + "(" + handler.eventType + ") " + handler.priority
                        + (handler.receiveCanceled ? " receiveCanceled" : ""));
            }
        }
        assertFalse(lines.isEmpty());
        SnapshotSupport.assertSnapshot("event-subscriber-wiring.txt", lines);
    }

    @Test
    public void everySubscribeMethodIsPublicStaticWithOneParameter() throws Exception {
        TreeSet<String> problems = new TreeSet<>();
        for (Subscriber subscriber : subscribers()) {
            for (Handler handler : subscriber.handlers) {
                if (!handler.isPublic || !handler.isStatic || handler.parameterCount != 1) {
                    problems.add(subscriber.name + "#" + handler.name);
                }
            }
        }
        assertTrue("not public static single-parameter: " + problems, problems.isEmpty());
    }

    @Test
    public void clientEventClassesDeclareClientDist() throws Exception {
        TreeSet<String> problems = new TreeSet<>();
        for (Subscriber subscriber : subscribers()) {
            String simple = subscriber.name.substring(subscriber.name.lastIndexOf('.') + 1);
            if (subscriber.name.contains(".client.") && simple.endsWith("Events")
                    && !subscriber.distText().equals("CLIENT")) {
                problems.add(subscriber.name);
            }
        }
        assertTrue("client Events classes without Dist.CLIENT only: " + problems, problems.isEmpty());
    }
}
