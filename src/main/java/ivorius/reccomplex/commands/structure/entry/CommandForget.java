/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.reccomplex.commands.structure.entry;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.commands.parameters.CommandSplit;
import ivorius.reccomplex.commands.parameters.RCExpect;
import ivorius.reccomplex.commands.parameters.RCParameters;
import ivorius.reccomplex.commands.parameters.SimpleCommand;
import ivorius.reccomplex.utils.ServerTranslations;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by lukas on 25.05.14.
 */
public class CommandForget extends CommandSplit
{
    public CommandForget()
    {
        add(new SimpleCommand("id", "<id>", () -> RCExpect.startRC().skip(1)) {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                RCParameters parameters = RCParameters.of(args);
                WorldStructureGenerationData generationData = WorldStructureGenerationData.get(sender.getEntityWorld());

                WorldStructureGenerationData.Entry entry = generationData.removeEntry(UUID.fromString(parameters.get().first().require()));

                if (entry == null)
                    throw ServerTranslations.commandException("commands.rcforget.unknown");
                else
                    sender.sendMessage(ServerTranslations.format("commands.rcforget.success", entry.description()));
            }
        });

        add(new SimpleCommand("all", "[x] [y] [z]", () -> RCExpect.startRC().xyz()) {
            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
            {
                RCParameters parameters = RCParameters.of(args);
                WorldStructureGenerationData generationData = WorldStructureGenerationData.get(sender.getEntityWorld());

                BlockPos pos = parameters.mc().pos(sender.getPosition(), false).require();

                List<WorldStructureGenerationData.Entry> entries = generationData.entriesAt(pos).collect(Collectors.toList());

                entries.forEach(e -> generationData.removeEntry(e.getUuid()));

                if (entries.size() == 1)
                    sender.sendMessage(ServerTranslations.format("commands.rcforget.success", entries.get(0).description()));
                else
                    sender.sendMessage(ServerTranslations.format("commands.rcforgetall.success", entries.size()));
            }
        });
    }

    @Override
    public String getName()
    {
        return RCConfig.commandPrefix + "forget";
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }
}