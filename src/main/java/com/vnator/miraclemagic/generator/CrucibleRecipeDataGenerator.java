package com.vnator.miraclemagic.generator;

import com.vnator.miraclemagic.MiracleMagic;

import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = MiracleMagic.MODID)
public class CrucibleRecipeDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        // event.getGenerator().addProvider(
        // event.includeServer(),
        // output ->
        // );
    }

    class CrucibleRecipeBuilder implements RecipeBuilder {

        @Override
        public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
            return this;
        }

        @Override
        public RecipeBuilder group(@Nullable String groupName) {
            return this;
        }

        @Override
        public Item getResult() {
            return null;
        }

        @Override
        public void save(RecipeOutput recipeOutput, ResourceLocation p_176504_) {}
    }
}
