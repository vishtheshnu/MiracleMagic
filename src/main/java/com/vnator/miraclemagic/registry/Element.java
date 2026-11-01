package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Base representation of an Element (aka Aspect)
 * TODO remove name field, the file name/ResourceLocation is the name.
 * TODO add fields for icon, and the 2 Elements used to create this one
 */
public record Element(String name) {

    public static final ResourceKey<Registry<Element>> REGISTRY_KEY = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "elements"));

    public static final Codec<Element> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(Element::name)).apply(instance, Element::new));
    // TODO add icon and what 2 other Essentia are used to create this
}
