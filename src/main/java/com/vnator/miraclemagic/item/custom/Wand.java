package com.vnator.miraclemagic.item.custom;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.component.ModDataComponents;
import com.vnator.miraclemagic.registry.ElementPoints;
import com.vnator.miraclemagic.util.VisHelper;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Wand item that stores Vis and is used in crafting, spellcasting, multiblock assembly, etc.
 * Vis only comes in 6 elements: Air, Water, Earth, Fire, Order, Entropy
 * This is hardcoded, but can be changed to be configurable in future versions. Not recommended, though.
 */
public class Wand extends Item {

    private static final ResourceLocation AIR = ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "air");
    private static final ResourceLocation WATER = ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "water");
    private static final ResourceLocation EARTH = ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "earth");
    private static final ResourceLocation FIRE = ResourceLocation.fromNamespaceAndPath(MiracleMagic.MODID, "fire");

    private static final int WATER_COLOR = 0x0000FF; // blue
    private static final int EARTH_COLOR = 0x6B4F0A; // brown
    private static final int FIRE_COLOR = 0xFF0000; // red

    private int capacity;
    private float [] costMultiplier;

    public Wand(Properties properties, int capacity, float[] costMultiplier) {
        super(properties
                .stacksTo(1));
        this.capacity = capacity;
        this.costMultiplier = costMultiplier;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        //context.getItemInHand().get()
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(itemStack, context, components, flag);
        // A wand with no VIS component yet (eg. freshly crafted) is treated as empty
        ElementPoints vis = itemStack.get(ModDataComponents.VIS);

        components.add(elementLine(vis, VisHelper.AIR));
        components.add(elementLine(vis, VisHelper.WATER));
        components.add(elementLine(vis, VisHelper.EARTH));
        components.add(elementLine(vis, VisHelper.FIRE));
        components.add(elementLine(vis, VisHelper.ORDER));
        components.add(elementLine(vis, VisHelper.ENTROPY));
    }

    /**
     * Creates a tooltip line of the element's translated name followed by its amount, eg. "Air: 12"
     *
     * @param vis     The wand's stored vis, or null if the wand has none
     * @param element Registry id of the element to display
     */
    private static MutableComponent elementLine(@Nullable ElementPoints vis, VisHelper element) {
        ResourceLocation location = ResourceLocation.parse(element.location);
        int amount = vis == null ? 0 : vis.amounts().entrySet().stream()
                .filter(entry -> entry.getKey().is(location))
                .mapToInt(Map.Entry::getValue)
                .findFirst().orElse(0);


        return Component.translatable(Util.makeDescriptionId("element", location)).withColor(element.color)
                .append(Component.literal(": " + amount));
    }

}
