package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

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

    /**
     * Merges the element:points of {@code other} into this instance
     * 
     * @param other    Instance of Essentia to merge into this
     * @param multiple How many times the other Essentia's amounts should be added. Used when merging multiple essentia
     *                 together
     */
    public void merge(Essentia other, int multiple) {
        other.points.forEach(((element, amount) -> {
            if (!points.containsKey(element)) {
                points.put(element, 0);
            }
            points.put(element, points.get(element) + (amount * multiple));
        }));
    }

    @Override
    public @NotNull String toString() {
        StringBuilder msg = new StringBuilder("Essentia: [");
        for (Holder<Element> element : points.keySet()) {
            msg.append("\n").append("\t").append(element.value().name()).append(":").append(points.get(element));
        }
        msg.append("\n]");
        return msg.toString();
    }
}
