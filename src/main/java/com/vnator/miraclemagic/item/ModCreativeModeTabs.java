package com.vnator.miraclemagic.item;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.block.ModBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, MiracleMagic.MODID);

    public static final Supplier<CreativeModeTab> MOD_TAB = CREATIVE_MODE_TAB.register("miracle_magic_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.EXAMPLE_BLOCK.asItem()))
                    .title(Component.translatable("creativetab.miraclemagic.mod_tab"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.EXAMPLE_BLOCK.asItem());
                        output.accept(ModBlocks.CRUCIBLE.asItem());
                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
