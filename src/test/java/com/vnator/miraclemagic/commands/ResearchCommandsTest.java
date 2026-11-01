package com.vnator.miraclemagic.commands;

import com.vnator.miraclemagic.MiracleMagic;
import com.vnator.miraclemagic.TestUtil;
import com.vnator.miraclemagic.attachment.ModDataAttachments;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Set;

@GameTestHolder(MiracleMagic.MODID)
@PrefixGameTestTemplate(false)
public class ResearchCommandsTest {

    /**
     * TODO Tests:
     * All three commands, get - nothing, add, get - one thing, remove, get - nothing
     * Permission, confirm it fails (and returns 0 + relevant message) for add and remove if player isn't at least
     * permission level 2
     * Get works with any permission level
     * Add stage already present
     * Remove stage not present
     */

    private static final String TEST_RESEARCH = "test_research";

    // Instance fields are safe here: GameTestRegistry creates a new instance of this class for every
    // non-static test method invocation, so this state is scoped to a single test run.
    private GameTestHelper helper;
    private ServerPlayer player;
    private CommandSourceStack elevatedSource;
    private CommandSourceStack lowSource;

    private void setup(GameTestHelper helper) {
        this.helper = helper;
        // makeMockServerPlayerInLevel() creates actual player, required for commands to work
        player = TestUtil.createPlayer(helper);
        elevatedSource = player.createCommandSourceStack().withPermission(2);
        // Level 1 is the highest level that should still be rejected by add/remove
        lowSource = player.createCommandSourceStack().withPermission(1);
    }

    private void teardown() {
        helper.succeed();
        player.discard();
        // Required for players to be fully removed, which allows getting player name via player.getScoreboardName() to
        // work correctly
        helper.getLevel().getServer().getPlayerList().remove(player);
    }

    @GameTest(template = "empty")
    public void allCommandsTest(GameTestHelper helper) {
        setup(helper);

        assertPlayerResearchEquals(Set.of(), "player research at start");

        runCommand(elevatedSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after add");

        runCommand(elevatedSource, removeCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(), "player research after remove");

        teardown();
    }

    @GameTest(template = "empty")
    public void getSucceedsWithInsufficientPermissionTest(GameTestHelper helper) {
        setup(helper);

        assertCommandSucceeds(lowSource, getCommand());

        teardown();
    }

    @GameTest(template = "empty")
    public void addFailsWithInsufficientPermissionTest(GameTestHelper helper) {
        setup(helper);

        assertCommandFails(lowSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(), "player research after unauthorized add");

        teardown();
    }

    @GameTest(template = "empty")
    public void removeFailsWithInsufficientPermissionTest(GameTestHelper helper) {
        setup(helper);

        // Give the player research to remove, using a source that is allowed to add it
        runCommand(elevatedSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after setup add");

        assertCommandFails(lowSource, removeCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after unauthorized remove");

        teardown();
    }

    @GameTest(template = "empty")
    public void addFailsWithRepeatStage(GameTestHelper helper) {
        setup(helper);

        runCommand(elevatedSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after setup add");

        int result = runCommand(elevatedSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after repeat add");
        helper.assertValueEqual(result, 0, "repeat get result code");

        teardown();
    }

    @GameTest(template = "empty")
    public void removeFailsWithRepeatCall(GameTestHelper helper) {
        setup(helper);

        runCommand(elevatedSource, addCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(TEST_RESEARCH), "player research after setup add");

        int result = runCommand(elevatedSource, removeCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(), "player research after first removal");
        helper.assertValueEqual(result, 1, "first stage remove result code");

        result = runCommand(elevatedSource, removeCommand(TEST_RESEARCH));
        assertPlayerResearchEquals(Set.of(), "player research after second removal");
        helper.assertValueEqual(result, 0, "second  stage remove result code");

        teardown();
    }

    private String addCommand(String research) {
        return "miraclemagic research add " + research + " " + player.getScoreboardName();
    }

    private String removeCommand(String research) {
        return "miraclemagic research remove " + research + " " + player.getScoreboardName();
    }

    private String getCommand() {
        return "miraclemagic research get " + player.getScoreboardName();
    }

    /**
     * Runs the command through the dispatcher directly so parse/execution errors fail the test, instead of being
     * swallowed like they are by performPrefixedCommand.
     */
    private int runCommand(CommandSourceStack source, String command) {
        try {
            return getDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            helper.fail("Command failed: " + command + " (" + e.getMessage() + ")");
        }
        return -1;
    }

    private CommandDispatcher<CommandSourceStack> getDispatcher() {
        return helper.getLevel().getServer().getCommands().getDispatcher();
    }

    /**
     * Runs the command through the dispatcher directly, since performPrefixedCommand swallows failures.
     * A source that fails a node's requires() check can't see that node, so parsing fails with a
     * CommandSyntaxException.
     */
    private void assertCommandFails(CommandSourceStack source, String command) {
        try {
            getDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            return;
        }
        helper.fail("Expected command to fail: " + command);
    }

    private void assertCommandSucceeds(CommandSourceStack source, String command) {
        try {
            getDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            helper.fail("Expected command to succeed: " + command);
        }
    }

    private void assertPlayerResearchEquals(Set<String> expected, String name) {
        // The mapped parameter names of assertValueEqual are swapped: its error message treats the first
        // argument as the actual value, so actual is passed first here.
        helper.assertValueEqual(player.getData(ModDataAttachments.PLAYER_RESEARCH_STAGES), expected, name);
    }
}
