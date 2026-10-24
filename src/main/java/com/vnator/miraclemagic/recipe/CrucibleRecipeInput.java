package com.vnator.miraclemagic.recipe;

import com.vnator.miraclemagic.registry.Essentia;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.Set;

public record CrucibleRecipeInput(ItemStack input, Essentia essentia, Set<String> playerResearch)
        implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
