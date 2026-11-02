package com.vnator.miraclemagic.util;

import com.vnator.miraclemagic.registry.ElementPoints;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public enum VisHelper {
    AIR("miraclemagic:air", 0xfff98f),
    WATER("miraclemagic:water", 0x0000ff),
    EARTH("miraclemagic:earth", 0x6b4f0a),
    FIRE("miraclemagic:fire", 0xff0000),
    ORDER("miraclemagic:order", 0xffffff),
    ENTROPY("miraclemagic:entropy", 0x474747);

    public String location;
    public int color;

    private VisHelper(String location, int color) {
        this.location = location;
        this.color = color;
    }
}
