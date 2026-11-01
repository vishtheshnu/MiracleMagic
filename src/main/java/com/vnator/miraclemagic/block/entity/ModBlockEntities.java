package com.vnator.miraclemagic.block.entity;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.block.ModBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
            .create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MiracleMagic.MODID);

    public static final Supplier<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE_BE = BLOCK_ENTITIES
            .register("crucible_be",
                    () -> BlockEntityType.Builder.of(CrucibleBlockEntity::new, ModBlocks.CRUCIBLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
