package com.vnator.miraclemagic;

import com.vnator.miraclemagic.attachment.ModDataAttachments;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestListener;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.HashSet;
import java.util.UUID;

public class TestUtil {

    public static ServerPlayer createPlayerWithResearch(GameTestHelper helper, HashSet<String> research) {
        ServerPlayer player = createPlayer(helper);
        player.setData(ModDataAttachments.PLAYER_RESEARCH_STAGES, research);
        return player;
    }

    /**
     * Copies the deprecated GameTestHelper.makeMockServerPlayerInLevel()
     * 
     * @param helper
     * @return newly created ServerPlayer, which must be destroyed and removed at the end of each test
     */
    public static ServerPlayer createPlayer(GameTestHelper helper) {
        String playerName = "mock-" + UUID.randomUUID().toString().substring(0, 8);
        CommonListenerCookie commonlistenercookie = CommonListenerCookie
                .createInitial(new GameProfile(UUID.randomUUID(), playerName), false);
        ServerPlayer serverplayer = new ServerPlayer(
                helper.getLevel().getServer(), helper.getLevel(), commonlistenercookie.gameProfile(),
                commonlistenercookie.clientInformation()) {

            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }
        };
        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        PlayerList playerList = helper.getLevel().getServer().getPlayerList();
        playerList.placeNewPlayer(connection, serverplayer, commonlistenercookie);

        // Remove the player once the test finishes, whether it passed, failed, or timed out. Listeners are notified
        // by GameTestInfo.tick() after the test is done, so this also covers succeedWhen()/sequence-based tests.
        helper.testInfo.addListener(new GameTestListener() {

            @Override
            public void testStructureLoaded(GameTestInfo testInfo) {}

            @Override
            public void testPassed(GameTestInfo test, GameTestRunner runner) {
                removeMockPlayer(playerList, serverplayer);
            }

            @Override
            public void testFailed(GameTestInfo test, GameTestRunner runner) {
                removeMockPlayer(playerList, serverplayer);
            }

            @Override
            public void testAddedForRerun(GameTestInfo oldTest, GameTestInfo newTest, GameTestRunner runner) {}
        });
        return serverplayer;
    }

    /**
     * Discards the player and removes it from the server. Safe to call more than once, eg. if a test already removed
     * its player manually.
     */
    public static void removeMockPlayer(PlayerList playerList, ServerPlayer player) {
        if (playerList.getPlayer(player.getUUID()) != player) {
            return;
        }
        player.discard();
        playerList.remove(player);
    }
}
