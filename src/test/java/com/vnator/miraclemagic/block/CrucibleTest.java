package com.vnator.miraclemagic.block;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.TestUtil;
import com.vnator.miraclemagic.block.entity.CrucibleBlockEntity;
import com.vnator.miraclemagic.registry.Element;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * If tests from this class begin to fail suddenly, change the recipes, essentia, etc. checked to match latest actual
 * recipes
 * TODO complete writing tests after recipes and datagen is fully implemented
 */
@GameTestHolder(MiracleMagic.MODID)
@PrefixGameTestTemplate(false)
public class CrucibleTest {

    // crucible_test is 1x2x1: a crucible with an air block above it. GameTest positions are relative to the
    // structure block, and the template is placed one block above it, so the template's y=0 is relative y=1.
    private static final BlockPos CRUCIBLE_POS = new BlockPos(0, 1, 0);
    private static final Vec3 TOP_OF_STRUCTURE = new Vec3(0.5, 2.5, 0.5);

    @GameTest(template = "crucible_test")
    public void itemWithEssentia_IsConsumed(GameTestHelper helper) {
        // Sand's essentia is defined in the essentia_items data map: air 1, earth 2
        ItemEntity sand = helper.spawnItem(Items.SAND, TOP_OF_STRUCTURE);

        // The item falls onto the crucible, which consumes it via stepOn() and stores its essentia
        helper.succeedWhen(() -> {
            helper.assertTrue(sand.isRemoved(), "Expected sand item entity to be consumed by the crucible");

            CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
            helper.assertValueEqual(getEssentiaByName(crucible),
                    Map.of("miraclemagic:air", 1, "miraclemagic:earth", 2), "crucible essentia");
        });
    }

    @GameTest(template = "crucible_test")
    public void itemWithoutEssentia_NotConsumed(GameTestHelper helper) {
        // Structure Void has no essentia
        ItemEntity structureVoid = helper.spawnItem(Blocks.STRUCTURE_VOID.asItem(), TOP_OF_STRUCTURE);

        // The item falls onto the crucible, which should leave it alone since it has no essentia. Checking a
        // negative needs a fixed wait rather than a condition: give the item time to land and sit on the crucible.
        helper.startSequence()
                .thenExecuteAfter(20, () -> {
                    helper.assertFalse(structureVoid.isRemoved(),
                            "Expected structure void item entity to NOT be consumed by the crucible");

                    CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
                    helper.assertValueEqual(getEssentiaByName(crucible), Map.of(), "crucible essentia");
                })
                .thenSucceed();
    }

    @GameTest(template = "crucible_test")
    public void itemStackWithEssentia_Consumed(GameTestHelper helper) {
        // Sand's essentia is defined in the essentia_items data map: air 1, earth 2
        ItemEntity sand = helper.spawnItem(Items.SAND, TOP_OF_STRUCTURE);
        sand.setItem(new ItemStack(Items.SAND, 64));

        // The item falls onto the crucible, which consumes it via stepOn() and stores its essentia
        helper.succeedWhen(() -> {
            helper.assertTrue(sand.isRemoved(), "Expected sand item entity to be consumed by the crucible");

            CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
            helper.assertValueEqual(getEssentiaByName(crucible),
                    Map.of("miraclemagic:air", 64, "miraclemagic:earth", 128), "crucible essentia");
        });
    }

    @GameTest(template = "crucible_test")
    public void itemStackWithoutEssentia_NotConsumed(GameTestHelper helper) {
        // Structure Void has no essentia
        ItemEntity structureVoid = helper.spawnItem(Blocks.STRUCTURE_VOID.asItem(), TOP_OF_STRUCTURE);
        structureVoid.setItem(new ItemStack(Blocks.STRUCTURE_VOID, 64));

        // The item falls onto the crucible, which should leave it alone since it has no essentia. Checking a
        // negative needs a fixed wait rather than a condition: give the item time to land and sit on the crucible.
        helper.startSequence()
                .thenExecuteAfter(20, () -> {
                    helper.assertFalse(structureVoid.isRemoved(),
                            "Expected structure void item entity to NOT be consumed by the crucible");
                    helper.assertValueEqual(structureVoid.getItem().getCount(), 64,
                            "Expected structure void item entity to contain full stack");

                    CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
                    helper.assertValueEqual(getEssentiaByName(crucible), Map.of(), "crucible essentia");
                })
                .thenSucceed();
    }

    @GameTest(template = "crucible_test")
    public void itemFromPlayer_craftSucceeds(GameTestHelper helper) {
        ServerPlayer player = TestUtil.createPlayerWithResearch(helper, new HashSet<>(Set.of("test2")));
        setCrucibleEssentia(helper, Map.of("miraclemagic:air", 5, "miraclemagic:earth", 10));
        ItemEntity catalyst = helper.spawnItem(Blocks.SPRUCE_LOG.asItem(), TOP_OF_STRUCTURE);
        catalyst.setThrower(player);

        helper.succeedWhen(() -> {
            helper.assertTrue(catalyst.isRemoved(), "Expected recipe catalyst to be removed");
            helper.assertTrue(player.getInventory().contains(new ItemStack(Items.DIAMOND)),
                    "Expected player inventory to contain crucible crafting result");
        });
    }

    @GameTest(template = "crucible_test")
    public void itemWithoutPlayer_craftFails(GameTestHelper helper) {
        ServerPlayer player = TestUtil.createPlayerWithResearch(helper, new HashSet<>(Set.of("test2")));
        setCrucibleEssentia(helper, Map.of("miraclemagic:air", 5, "miraclemagic:earth", 10));
        ItemEntity catalyst = helper.spawnItem(Blocks.SPRUCE_LOG.asItem(), TOP_OF_STRUCTURE);

        // Spruce log has no essentia, so without a recipe it should also be left alone rather than converted.
        // Negative check, so wait a fixed time for the item to land and sit on the crucible, well under the timeout.
        helper.startSequence()
                .thenExecuteAfter(20, () -> {
                    helper.assertFalse(catalyst.isRemoved(),
                            "Expected recipe catalyst to NOT be consumed without a player thrower");
                    helper.assertFalse(player.getInventory().contains(new ItemStack(Items.DIAMOND)),
                            "Expected player inventory to NOT contain crucible crafting result");

                    CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
                    helper.assertValueEqual(getEssentiaByName(crucible),
                            Map.of("miraclemagic:air", 5, "miraclemagic:earth", 10), "crucible essentia");
                })
                .thenSucceed();
    }

    @GameTest(template = "crucible_test")
    public void itemWithPlayerInsufficientEssentia_craftFails(GameTestHelper helper) {
        helper.succeed();
    }

    @GameTest(template = "crucible_test")
    public void saveAndLoadEssentia_CorrectIO(GameTestHelper helper) {
        helper.succeed();
    }

    /**
     * Replaces the crucible's stored essentia with exactly {@code essentiaByName}.
     *
     * @param essentiaByName Element registry id (eg. "miraclemagic:earth") to amount, matching the format returned by
     *                       {@link #getEssentiaByName}
     */
    private static void setCrucibleEssentia(GameTestHelper helper, Map<String, Integer> essentiaByName) {
        CrucibleBlockEntity crucible = helper.getBlockEntity(CRUCIBLE_POS);
        HolderLookup.RegistryLookup<Element> elements = helper.getLevel().registryAccess()
                .lookupOrThrow(Element.REGISTRY_KEY);

        Map<Holder<Element>, Integer> amounts = crucible.getEssentia().amounts();
        amounts.clear();
        essentiaByName.forEach((name, amount) -> amounts.put(
                elements.getOrThrow(ResourceKey.create(Element.REGISTRY_KEY, ResourceLocation.parse(name))), amount));
        crucible.setChanged();
    }

    private static Map<String, Integer> getEssentiaByName(CrucibleBlockEntity crucible) {
        Map<String, Integer> essentia = new HashMap<>();
        crucible.getEssentia().amounts()
                .forEach((element, amount) -> essentia.put(element.getKey().location().toString(), amount));
        return essentia;
    }
}
