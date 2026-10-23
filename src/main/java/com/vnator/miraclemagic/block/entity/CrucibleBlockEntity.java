package com.vnator.miraclemagic.block.entity;

import com.vnator.miraclemagic.registry.Element;
import com.vnator.miraclemagic.registry.Essentia;
import com.vnator.miraclemagic.registry.ModRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class CrucibleBlockEntity extends BlockEntity {

    private Essentia storedEssentia;

    public CrucibleBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRUCIBLE_BE.get(), pos, blockState);
        storedEssentia = new Essentia(new HashMap<>());
    }

    /**
     * Accepts an input ItemStack to convert into essentia or perform a recipe.
     * Expected to only be called by Server
     * 
     * @param input Inserted ItemStack to process
     * @return Leftover inputs that were not consumed
     */
    public ItemStack acceptInput(ItemStack input) {
        Essentia inputEssentia = BuiltInRegistries.ITEM.wrapAsHolder(input.getItem())
                .getData(ModRegistries.ESSENTIA_ITEMS);
        if (inputEssentia == null || inputEssentia.points().isEmpty()) {
            return input;
        }

        storedEssentia.merge(inputEssentia, input.getCount());
        setChanged();
        return ItemStack.EMPTY;
    }

    public Essentia getEssentia() {
        return storedEssentia;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag essentiaTag = new CompoundTag();
        storedEssentia.points().forEach((element, amount) -> {
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

        storedEssentia = new Essentia(essentiaMap);
    }
}
