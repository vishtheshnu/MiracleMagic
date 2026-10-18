package com.vnator.miraclemagic.attachment;

import com.vnator.miraclemagic.MiracleMagic;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import com.mojang.serialization.Codec;

import java.util.HashSet;

public class ModDataAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, MiracleMagic.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<HashSet<String>>> PLAYER_RESEARCH_STAGES = ATTACHMENT_TYPES
            .register("player_strings", () -> AttachmentType.builder(() -> new HashSet<String>())
                    .serialize(Codec.STRING.listOf().xmap(HashSet::new, data -> data.stream().toList()))
                    .copyOnDeath()
                    .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
