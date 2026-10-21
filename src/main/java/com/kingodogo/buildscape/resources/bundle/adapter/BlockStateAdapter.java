package com.kingodogo.buildscape.resources.bundle.adapter;

import com.kingodogo.buildscape.resources.bundle.blockstate.BlockStateIR;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.AndCondition;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.client.renderer.block.model.multipart.KeyValueCondition;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.OrCondition;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BlockStateAdapter {

    public static BlockModelDefinition toBlockModelDefinition(BlockStateIR ir,
            StateDefinition<Block, BlockState> definition) {
        if (ir == null) {
            return null;
        }

        Map<String, MultiVariant> variants = new LinkedHashMap<>();
        if (ir.variants() != null) {
            for (Map.Entry<String, List<BlockStateIR.VariantModelIR>> entry : ir.variants().entrySet()) {
                variants.put(entry.getKey(), toMultiVariant(entry.getValue()));
            }
        }

        MultiPart multiPart = null;
        if (ir.type() == BlockStateIR.TYPE_MULTIPART && ir.multiparts() != null && definition != null) {
            List<Selector> selectors = new ArrayList<>(ir.multiparts().size());
            for (BlockStateIR.MultiPartIR mp : ir.multiparts()) {
                Condition cond = toCondition(mp.condition());
                MultiVariant mv = toMultiVariant(mp.models());
                selectors.add(new Selector(cond, mv));
            }
            multiPart = new MultiPart(definition, selectors);
        }

        return new BlockModelDefinition(variants, multiPart);
    }

    private static MultiVariant toMultiVariant(List<BlockStateIR.VariantModelIR> list) {
        if (list == null || list.isEmpty()) {
            return new MultiVariant(Collections.emptyList());
        }
        List<Variant> variants = new ArrayList<>(list.size());
        for (BlockStateIR.VariantModelIR m : list) {
            ResourceLocation loc = new ResourceLocation(m.model());
            BlockModelRotation rot = BlockModelRotation.by(m.x(), m.y());
            variants.add(new Variant(loc, rot.getRotation(), m.uvLock(), m.weight()));
        }
        return new MultiVariant(variants);
    }

    private static Condition toCondition(BlockStateIR.ConditionIR cond) {
        if (cond == null) {
            return Condition.TRUE;
        }
        if (cond.kind() == BlockStateIR.ConditionIR.KIND_KEY_VALUE) {
            return new KeyValueCondition(cond.key(), cond.value());
        } else if (cond.kind() == BlockStateIR.ConditionIR.KIND_AND) {
            List<Condition> terms = new ArrayList<>(cond.terms().size());
            for (BlockStateIR.ConditionIR term : cond.terms()) {
                terms.add(toCondition(term));
            }
            return new AndCondition(terms);
        } else if (cond.kind() == BlockStateIR.ConditionIR.KIND_OR) {
            List<Condition> terms = new ArrayList<>(cond.terms().size());
            for (BlockStateIR.ConditionIR term : cond.terms()) {
                terms.add(toCondition(term));
            }
            return new OrCondition(terms);
        }
        return Condition.TRUE;
    }
}
