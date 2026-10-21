package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;

import java.util.Map;

/**
 * Represents the Essentia in something (eg. item or entity)
 * 
 * @param points Number of points of each element something contains (eg. 1 earth, 1 air)
 *
 */
public record Essentia(Map<Holder<Element>, Integer> points) {

    public static final ResourceKey<Registry<Essentia>> REGISTRY_KEY_ITEMS = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "essentia_items"));
    public static final ResourceKey<Registry<Essentia>> REGISTRY_KEY_ENTITIES = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "essentia_entities"));

    public static final Codec<Essentia> CODEC = Codec
            .unboundedMap(RegistryFileCodec.create(Element.REGISTRY_KEY, Element.CODEC), Codec.INT)
            .xmap(Essentia::new, Essentia::points);
}
