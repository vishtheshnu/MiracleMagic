package com.vnator.miraclemagic.generator;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.registry.Element;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import static com.vnator.miraclemagic.registry.Element.REGISTRY_KEY;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class ElementDataGenerator {

    public static final RegistrySetBuilder RSB = new RegistrySetBuilder()
            .add(REGISTRY_KEY, bootstrap -> {
                registerElement(bootstrap, "air");
                registerElement(bootstrap, "water");
                registerElement(bootstrap, "earth");
                registerElement(bootstrap, "fire");
                registerElement(bootstrap, "entropy");
                registerElement(bootstrap, "order");
            });

    private static void registerElement(BootstrapContext<Element> boostrap, String name) {
        ResourceKey<Element> key = ResourceKey.create(REGISTRY_KEY,
                ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, name));
        boostrap.register(key, new Element(name));
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.createDatapackRegistryObjects(RSB);
    }
}
