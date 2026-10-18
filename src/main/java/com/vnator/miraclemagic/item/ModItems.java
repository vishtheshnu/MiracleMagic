package com.vnator.miraclemagic.item;

import com.vnator.miraclemagic.MiracleMagic;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MiracleMagic.MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
