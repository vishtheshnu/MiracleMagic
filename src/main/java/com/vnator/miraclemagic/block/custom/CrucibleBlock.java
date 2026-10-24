package com.vnator.miraclemagic.block.custom;

import com.vnator.miraclemagic.block.entity.CrucibleBlockEntity;
import com.vnator.miraclemagic.registry.Essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlock extends BaseEntityBlock {

    public static final MapCodec<CrucibleBlock> CODEC = simpleCodec(CrucibleBlock::new);

    public CrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    /* Block Entity */

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            // Call BlockEntity method to process
            if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucibleBlockEntity) {
                ItemStack output;
                if (itemEntity.getOwner() instanceof Player playerOwner) {
                    output = crucibleBlockEntity.acceptInput(itemEntity.getItem(), playerOwner);
                } else {
                    output = crucibleBlockEntity.acceptInput(itemEntity.getItem(), null);
                }

                itemEntity.setItem(output);
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        if (level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucibleBlockEntity) {
            Essentia essentia = crucibleBlockEntity.getEssentia();
            player.displayClientMessage(Component.literal(essentia.toString()), false);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // Create Flux/taint in the world when done
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
