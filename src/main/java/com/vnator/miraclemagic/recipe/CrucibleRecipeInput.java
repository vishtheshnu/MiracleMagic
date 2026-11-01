package com.vnator.miraclemagic.recipe;

import com.vnator.miraclemagic.registry.ElementPoints;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.Set;

public record CrucibleRecipeInput(ItemStack input, ElementPoints elementPoints, Set<String> playerResearch)
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
