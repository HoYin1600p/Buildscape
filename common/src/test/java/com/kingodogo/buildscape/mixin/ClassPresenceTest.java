package com.kingodogo.buildscape.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassPresenceTest {
    private static final String FIXTURE_NAME = ClassPresenceTest.class.getName() + "$PresenceFixture";
    private static boolean fixtureInitialized;

    @ParameterizedTest
    @ValueSource(strings = {
            "com.kingodogo.buildscape.mixin.BuildscapeMixinPlugin",
            "com.kingodogo.buildscape.client.performance.LaunchFasterInterop"
    })
    void findsPresentClassWithoutLoadingOrInitializingIt(String helperName) throws Exception {
        ProbeClassLoader loader = new ProbeClassLoader();
        Method classExists = classExistsMethod(loader, helperName);

        assertFalse(fixtureInitialized);
        assertTrue((boolean) classExists.invoke(null, FIXTURE_NAME));
        assertEquals(0, loader.fixtureLoadAttempts,
                "Presence checks must not load classes, even with initialization disabled");
        assertFalse(fixtureInitialized, "Presence checks must not run static initializers");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "com.kingodogo.buildscape.mixin.BuildscapeMixinPlugin",
            "com.kingodogo.buildscape.client.performance.LaunchFasterInterop"
    })
    void returnsFalseForMissingClass(String helperName) throws Exception {
        Method classExists = classExistsMethod(new ProbeClassLoader(), helperName);

        assertFalse((boolean) classExists.invoke(null,
                "com.kingodogo.buildscape.mixin.missing.NoSuchPresenceFixture"));
    }

    private static Method classExistsMethod(ProbeClassLoader loader, String helperName) throws Exception {
        Class<?> helper = loader.defineHelper(helperName);
        Method method = helper.getDeclaredMethod("classExists", String.class);
        method.setAccessible(true);
        return method;
    }

    // Referenced only by its binary name so the test does not load it before probing.
    static final class PresenceFixture {
        static {
            fixtureInitialized = true;
        }
    }

    private static final class ProbeClassLoader extends ClassLoader {
        private int fixtureLoadAttempts;

        private ProbeClassLoader() {
            super(ClassPresenceTest.class.getClassLoader());
        }

        private Class<?> defineHelper(String name) throws IOException {
            String resource = name.replace('.', '/') + ".class";
            try (InputStream input = getParent().getResourceAsStream(resource)) {
                assertNotNull(input, "Missing helper class resource: " + resource);
                byte[] bytes = input.readAllBytes();
                return defineClass(name, bytes, 0, bytes.length);
            }
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (FIXTURE_NAME.equals(name)) {
                fixtureLoadAttempts++;
            }
            return super.loadClass(name, resolve);
        }
    }
}
