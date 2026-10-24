package com.vnator.miraclemagic.registry;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    /**
     * Whether this Essentia holds >= points in all the aspects of Essentia other
     * 
     * @param other The Essentia instance to compare to
     * @return true if this contains at least as much Essentia as {@code other}
     */
    public boolean hasGreaterThan(Essentia other) {
        for (Holder<Element> element : other.points.keySet()) {
            if (points.get(element) < other.points.get(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Subtracts all the other essentia from this one. Will not subtract at all if it doesn't contain at least enough
     * 
     * @param other Essentia instance to subtract from this one
     * @return True if subtraction was successful, false if this contains less essentia than {@code other}
     */
    public boolean subtract(Essentia other) {
        if (!hasGreaterThan(other)) {
            return false;
        }
        for (Holder<Element> element : other.points.keySet()) {
            points.put(element, points.get(element) - other.points.get(element));
        }
        return true;
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

    public static final StreamCodec<RegistryFriendlyByteBuf, Essentia> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, Essentia>() {

        @Override
        public Essentia decode(RegistryFriendlyByteBuf buffer) {
            Map<Holder<Element>, Integer> essentiaMap = new HashMap<>();
            int entries = buffer.readInt();
            for (int i = 0; i < entries; i++) {
                ElementStack elementStack = ElementStack.STREAM_CODEC.decode(buffer);
                essentiaMap.put(elementStack.element, elementStack.amount);
            }

            return new Essentia(essentiaMap);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, Essentia value) {
            int entries = value.points().size();
            buffer.writeInt(entries);
            value.points().forEach((element, amount) -> {
                ElementStack.STREAM_CODEC.encode(buffer, new ElementStack(element, amount));
            });
        }

        record ElementStack(Holder<Element> element, Integer amount) {

            public static final StreamCodec<RegistryFriendlyByteBuf, ElementStack> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.holderRegistry(Element.REGISTRY_KEY), ElementStack::element,
                    ByteBufCodecs.INT, ElementStack::amount,
                    ElementStack::new);
        }
    };
}
