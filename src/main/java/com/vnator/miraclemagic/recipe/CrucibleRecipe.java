package com.vnator.miraclemagic.recipe;

import com.vnator.miraclemagic.registry.Element;
import com.vnator.miraclemagic.registry.Essentia;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CrucibleRecipe(Ingredient inputItem, Essentia essentia, String research, ItemStack output)
        implements Recipe<CrucibleRecipeInput> {

    @Override
    public boolean matches(CrucibleRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        // Input Item match
        if (!inputItem.test(input.input())) {
            return false;
        }
        // Essentia match
        Essentia crucibleEssentia = input.essentia();
        for (Holder<Element> element : essentia.points().keySet()) {
            if (crucibleEssentia.points().get(element) < essentia.points().get(element)) {
                return false;
            }
        }

        // Research match
        if (!input.playerResearch().contains(research)) {
            return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(CrucibleRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CRUCIBLE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CRUCIBLE_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {

        public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CrucibleRecipe::inputItem),
                Essentia.CODEC.fieldOf("essentia").forGetter(CrucibleRecipe::essentia),
                Codec.STRING.fieldOf("research").forGetter(CrucibleRecipe::research),
                ItemStack.CODEC.fieldOf("output").forGetter(CrucibleRecipe::output)).apply(inst, CrucibleRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CrucibleRecipe::inputItem,
                Essentia.STREAM_CODEC, CrucibleRecipe::essentia,
                ByteBufCodecs.STRING_UTF8, CrucibleRecipe::research,
                ItemStack.STREAM_CODEC, CrucibleRecipe::output,
                CrucibleRecipe::new);

        @Override
        public MapCodec<CrucibleRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
