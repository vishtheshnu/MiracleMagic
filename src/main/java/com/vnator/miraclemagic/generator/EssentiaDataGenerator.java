package com.vnator.miraclemagic.generator;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.registry.Element;
import com.vnator.miraclemagic.registry.Essentia;
import com.vnator.miraclemagic.registry.ModRegistries;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class EssentiaDataGenerator extends DataMapProvider {

    /**
     * Create a new provider.
     * TODO list of all element names here or in ElementDataGenerator to make registry lookup for each element automatic
     *
     * @param packOutput     the output location
     * @param lookupProvider a {@linkplain CompletableFuture} supplying the registries
     */
    protected EssentiaDataGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        Holder.Reference<Element> air = provider.lookup(Element.REGISTRY_KEY).get()
                .get(ResourceKey.create(Element.REGISTRY_KEY,
                        ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "air")))
                .get();

        Holder.Reference<Element> earth = provider.lookup(Element.REGISTRY_KEY).get()
                .get(ResourceKey.create(Element.REGISTRY_KEY,
                        ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "earth")))
                .get();
        builder(ModRegistries.ESSENTIA_ITEMS)
                .add(Items.SAND.builtInRegistryHolder(), new Essentia(Map.of(air, 1, earth, 2)), true);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(),
                new EssentiaDataGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
    }
}
