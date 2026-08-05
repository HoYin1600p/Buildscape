package com.kingodogo.buildscape.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class TransparentMultiBufferSource implements MultiBufferSource {
    private final MultiBufferSource parent;
    private final float alpha;

    public TransparentMultiBufferSource(MultiBufferSource parent, float alpha) {
        this.parent = parent;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        VertexConsumer parentConsumer = parent.getBuffer(renderType);
        return (VertexConsumer) Proxy.newProxyInstance(
            VertexConsumer.class.getClassLoader(),
            new Class<?>[]{VertexConsumer.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("color") && args != null && args.length == 4) {
                        if (args[0] instanceof Integer && args[1] instanceof Integer && args[2] instanceof Integer && args[3] instanceof Integer) {
                            int r = (Integer) args[0];
                            int g = (Integer) args[1];
                            int b = (Integer) args[2];
                            int a = (Integer) args[3];
                            int newA = (int) (a * alpha);
                            return method.invoke(parentConsumer, r, g, b, newA);
                        }
                        if (args[0] instanceof Float && args[1] instanceof Float && args[2] instanceof Float && args[3] instanceof Float) {
                            float r = (Float) args[0];
                            float g = (Float) args[1];
                            float b = (Float) args[2];
                            float a = (Float) args[3];
                            float newA = a * alpha;
                            return method.invoke(parentConsumer, r, g, b, newA);
                        }
                        if (args[0] instanceof Number && args[1] instanceof Number && args[2] instanceof Number && args[3] instanceof Number) {
                            // Catch-all for any other numeric types
                            float r = ((Number) args[0]).floatValue();
                            float g = ((Number) args[1]).floatValue();
                            float b = ((Number) args[2]).floatValue();
                            float a = ((Number) args[3]).floatValue();
                            float newA = a * alpha;
                            return method.invoke(parentConsumer, r, g, b, newA);
                        }
                    }
                    return method.invoke(parentConsumer, args);
                }
            }
        );
    }
}
