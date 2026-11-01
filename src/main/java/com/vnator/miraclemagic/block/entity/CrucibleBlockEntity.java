package com.vnator.miraclemagic.block.entity;

import com.vnator.miraclemagic.attachment.ModDataAttachments;
import com.vnator.miraclemagic.recipe.CrucibleRecipe;
import com.vnator.miraclemagic.recipe.CrucibleRecipeInput;
import com.vnator.miraclemagic.recipe.ModRecipes;
import com.vnator.miraclemagic.registry.Element;
import com.vnator.miraclemagic.registry.ElementPoints;
import com.vnator.miraclemagic.registry.ModRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public class CrucibleBlockEntity extends BlockEntity {

    private ElementPoints storedEssentia;

    public CrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRUCIBLE_BE.get(), pos, blockState);
        storedEssentia = new ElementPoints(new HashMap<>());
    }

    /**
     * Accepts an input ItemStack to convert into essentia or perform a recipe.
     * Expected to only be called by Server
     * 
     * @param input  Inserted ItemStack to process
     * @param player Player who inputted the item. If null, assumed to be inserted by non-player and will only convert
     *               to Essentia
     * @return Leftover inputs that were not consumed
     */
    public ItemStack acceptInput(ItemStack input, @Nullable Player player) {
        if (player != null) {
            Set<String> playerResearch = player.getData(ModDataAttachments.PLAYER_RESEARCH_STAGES);
            Optional<RecipeHolder<CrucibleRecipe>> optionalRecipe = this.level.getRecipeManager()
                    .getRecipeFor(ModRecipes.CRUCIBLE_RECIPE_TYPE.get(),
                            new CrucibleRecipeInput(input, storedEssentia, playerResearch), level);
            // Perform only one craft per tick this way
            if (optionalRecipe.isPresent()) {
                CrucibleRecipe recipe = optionalRecipe.get().value();

                boolean hasSubstance = storedEssentia.subtract(recipe.inputEssentia());
                if (hasSubstance) {
                    input.setCount(input.getCount() - 1);
                    player.addItem(recipe.output().copy());
                    return input;
                }
                // Turn ingredient into amounts if not enough to craft
            }
        }

        ElementPoints inputElementPoints = BuiltInRegistries.ITEM.wrapAsHolder(input.getItem())
                .getData(ModRegistries.ESSENTIA_ITEMS);
        if (inputElementPoints == null || inputElementPoints.amounts().isEmpty()) {
            return input;
        }

        // TODO play sound effect, louder/many times if input count > 1
        storedEssentia.merge(inputElementPoints, input.getCount());
        setChanged();
        return ItemStack.EMPTY;
    }

    public ElementPoints getEssentia() {
        return storedEssentia;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag essentiaTag = new CompoundTag();
        storedEssentia.amounts().forEach((element, amount) -> {
            essentiaTag.putInt(element.getKey().location().toString(), amount);
        });

        tag.put("essentia", essentiaTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        CompoundTag essentiaTag = tag.getCompound("essentia");

        Map<Holder<Element>, Integer> essentiaMap = new HashMap<>();
        essentiaTag.getAllKeys().forEach(location -> {
            Holder<Element> elementHolder = registries.lookup(Element.REGISTRY_KEY).orElseThrow()
                    .get(ResourceKey.create(Element.REGISTRY_KEY, ResourceLocation.parse(location))).orElseThrow();
            essentiaMap.put(elementHolder, essentiaTag.getInt(location));
        });

        storedEssentia = new ElementPoints(essentiaMap);
    }
}
