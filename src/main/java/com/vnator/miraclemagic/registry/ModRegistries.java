package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class ModRegistries {

    @SubscribeEvent
    public static void registryEvent(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Element.REGISTRY_KEY, Element.CODEC);
    }
}
