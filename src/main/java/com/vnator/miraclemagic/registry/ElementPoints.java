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
 * @param amounts Actual holder for element to number of points of that element (eg. earth:2, air:1)
 *
 */
public record ElementPoints(Map<Holder<Element>, Integer> amounts) {

    public static final ResourceKey<Registry<ElementPoints>> REGISTRY_KEY_ITEMS = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "essentia_items"));
    public static final ResourceKey<Registry<ElementPoints>> REGISTRY_KEY_ENTITIES = ResourceKey
            .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "essentia_entities"));

    public static final Codec<ElementPoints> CODEC = Codec
            .unboundedMap(RegistryFileCodec.create(Element.REGISTRY_KEY, Element.CODEC), Codec.INT)
            .xmap(ElementPoints::new, ElementPoints::amounts);

    public ElementPoints {
        amounts = new HashMap<>(amounts);
    }

    /**
     * Merges the element:amounts of {@code other} into this instance
     * 
     * @param other    Instance of Essentia to merge into this
     * @param multiple How many times the other Essentia's amounts should be added. Used when merging multiple essentia
     *                 together
     */
    public void merge(ElementPoints other, int multiple) {
        other.amounts.forEach(((element, amount) -> {
            if (!amounts.containsKey(element)) {
                amounts.put(element, 0);
            }
            amounts.put(element, amounts.get(element) + (amount * multiple));
        }));
    }

    /**
     * Whether this Essentia holds >= amounts in all the aspects of Essentia other
     * 
     * @param other The Essentia instance to compare to
     * @return true if this contains at least as much Essentia as {@code other}
     */
    public boolean hasGreaterThan(ElementPoints other) {
        for (Holder<Element> element : other.amounts.keySet()) {
            if (amounts.getOrDefault(element, 0) < other.amounts.get(element)) {
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
    public boolean subtract(ElementPoints other) {
        if (!hasGreaterThan(other)) {
            return false;
        }
        for (Holder<Element> element : other.amounts.keySet()) {
            amounts.put(element, amounts.get(element) - other.amounts.get(element));
        }
        return true;
    }

    @Override
    public @NotNull String toString() {
        StringBuilder msg = new StringBuilder("Essentia: [");
        for (Holder<Element> element : amounts.keySet()) {
            msg.append("\n").append("\t").append(element.value().name()).append(":").append(amounts.get(element));
        }
        msg.append("\n]");
        return msg.toString();
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, ElementPoints> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ElementPoints>() {

        @Override
        public ElementPoints decode(RegistryFriendlyByteBuf buffer) {
            Map<Holder<Element>, Integer> essentiaMap = new HashMap<>();
            int entries = buffer.readInt();
            for (int i = 0; i < entries; i++) {
                ElementStack elementStack = ElementStack.STREAM_CODEC.decode(buffer);
                essentiaMap.put(elementStack.element, elementStack.amount);
            }

            return new ElementPoints(essentiaMap);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ElementPoints value) {
            int entries = value.amounts().size();
            buffer.writeInt(entries);
            value.amounts().forEach((element, amount) -> {
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
