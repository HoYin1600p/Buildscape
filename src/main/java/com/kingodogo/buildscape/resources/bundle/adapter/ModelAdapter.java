package com.kingodogo.buildscape.resources.bundle.adapter;

import com.kingodogo.buildscape.resources.bundle.model.ModelIR;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ModelAdapter {

    public static BlockModel toBlockModel(ModelIR ir) {
        if (ir == null) {
            return null;
        }

        ResourceLocation parentLoc = ir.parent() != null ? new ResourceLocation(ir.parent()) : null;

        // Elements
        List<BlockElement> elements = new ArrayList<>();
        if (ir.elements() != null) {
            for (ModelIR.ElementIR el : ir.elements()) {
                Vector3f from = new Vector3f(el.from()[0], el.from()[1], el.from()[2]);
                Vector3f to = new Vector3f(el.to()[0], el.to()[1], el.to()[2]);

                Map<Direction, BlockElementFace> faces = new HashMap<>();
                for (Map.Entry<Byte, ModelIR.FaceIR> fEntry : el.faces().entrySet()) {
                    Direction dir = Direction.from3DDataValue(fEntry.getKey());
                    ModelIR.FaceIR faceIR = fEntry.getValue();
                    Direction cull = faceIR.cullface() != null ? Direction.from3DDataValue(faceIR.cullface()) : null;
                    BlockFaceUV uv = new BlockFaceUV(faceIR.uv(), faceIR.rotation());
                    faces.put(dir, new BlockElementFace(cull, faceIR.tintIndex(), faceIR.texture(), uv));
                }

                BlockElementRotation rot = null;
                if (el.rotation() != null) {
                    Vector3f origin = new Vector3f(
                            el.rotation().origin()[0] * 0.0625f,
                            el.rotation().origin()[1] * 0.0625f,
                            el.rotation().origin()[2] * 0.0625f);
                    Direction.Axis axis = Direction.Axis.values()[el.rotation().axis()];
                    rot = new BlockElementRotation(origin, axis, el.rotation().angle(), el.rotation().rescale());
                }

                elements.add(new BlockElement(from, to, faces, rot, el.shade()));
            }
        }

        // Texture map
        Map<String, Either<Material, String>> textures = new LinkedHashMap<>();
        if (ir.textures() != null) {
            for (Map.Entry<String, String> entry : ir.textures().entrySet()) {
                String target = entry.getValue();
                if (target.startsWith("#")) {
                    textures.put(entry.getKey(), Either.right(target.substring(1)));
                } else {
                    Material mat = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(target));
                    textures.put(entry.getKey(), Either.left(mat));
                }
            }
        }

        BlockModel.GuiLight guiLight = switch (ir.guiLight()) {
            case ModelIR.GUI_LIGHT_FRONT -> BlockModel.GuiLight.FRONT;
            case ModelIR.GUI_LIGHT_SIDE -> BlockModel.GuiLight.SIDE;
            default -> null; // Inherit from parent
        };

        boolean ao = ir.ambientOcclusion() != null ? ir.ambientOcclusion() : true;

        ItemTransforms itemTransforms = toItemTransforms(ir.transforms());

        List<ItemOverride> overrides = new ArrayList<>();
        if (ir.overrides() != null) {
            for (ModelIR.OverrideIR ov : ir.overrides()) {
                List<ItemOverride.Predicate> preds = new ArrayList<>();
                if (ov.predicates() != null) {
                    for (Map.Entry<String, Float> p : ov.predicates().entrySet()) {
                        preds.add(new ItemOverride.Predicate(new ResourceLocation(p.getKey()), p.getValue()));
                    }
                }
                overrides.add(new ItemOverride(new ResourceLocation(ov.model()), preds));
            }
        }

        return new BlockModel(parentLoc, elements, textures, ao, guiLight, itemTransforms, overrides);
    }

    private static ItemTransforms toItemTransforms(Map<String, ModelIR.TransformIR> transforms) {
        if (transforms == null || transforms.isEmpty()) {
            return ItemTransforms.NO_TRANSFORMS;
        }

        ItemTransform thirdperson_r = toItemTransform(transforms.get("thirdperson_righthand"));
        ItemTransform thirdperson_l = toItemTransform(transforms.get("thirdperson_lefthand"));
        ItemTransform firstperson_r = toItemTransform(transforms.get("firstperson_righthand"));
        ItemTransform firstperson_l = toItemTransform(transforms.get("firstperson_lefthand"));
        ItemTransform head = toItemTransform(transforms.get("head"));
        ItemTransform gui = toItemTransform(transforms.get("gui"));
        ItemTransform ground = toItemTransform(transforms.get("ground"));
        ItemTransform fixed = toItemTransform(transforms.get("fixed"));

        return new ItemTransforms(thirdperson_l, thirdperson_r, firstperson_l, firstperson_r, head, gui, ground, fixed);
    }

    private static ItemTransform toItemTransform(ModelIR.TransformIR t) {
        if (t == null) {
            return ItemTransform.NO_TRANSFORM;
        }
        Vector3f rot = new Vector3f(t.rotation()[0], t.rotation()[1], t.rotation()[2]);
        Vector3f trans = new Vector3f(
                t.translation()[0] * 0.0625f,
                t.translation()[1] * 0.0625f,
                t.translation()[2] * 0.0625f);
        trans.clamp(-5.0f, 5.0f);
        Vector3f scale = new Vector3f(t.scale()[0], t.scale()[1], t.scale()[2]);
        scale.clamp(-4.0f, 4.0f);
        return new ItemTransform(rot, trans, scale);
    }
}
