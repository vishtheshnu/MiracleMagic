package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class ModRegistries {

    public static final DataMapType<Item, Essentia> ESSENTIA_ITEMS = DataMapType.builder(
            Essentia.REGISTRY_KEY_ITEMS.location(), Registries.ITEM, Essentia.CODEC).build();
    public static final DataMapType<Item, Essentia> ESSENTIA_ENTITIES = DataMapType.builder(
            Essentia.REGISTRY_KEY_ENTITIES.location(), Registries.ITEM, Essentia.CODEC).build();

    @SubscribeEvent
    public static void registryEvent(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Element.REGISTRY_KEY, Element.CODEC);
    }

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ESSENTIA_ITEMS);
        event.register(ESSENTIA_ENTITIES);
    }
}
