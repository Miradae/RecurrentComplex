/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.reccomplex.commands.clipboard;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.RCExpect;
import ivorius.reccomplex.commands.parameters.RCParameters;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.ServerTranslations;
import ivorius.reccomplex.world.gen.feature.structure.OperationGenerateStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by lukas on 25.05.14.
 */
public class CommandPaste extends CommandBase
{
    private String name;
    private String usage;

    public CommandPaste(String name, String usage)
    {
        this.name = name;
        this.usage = usage;
    }

    @Override
    public String getCommandName()
    {
        return RCConfig.commandPrefix + name;
    }

    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return ServerTranslations.usage(usage);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException
    {
        RCParameters parameters = RCParameters.of(args, "mirror", "generate");

        EntityPlayerMP entityPlayerMP = getCommandSenderAsPlayer(commandSender);
        RCEntityInfo entityInfo = RCCommands.getStructureEntityInfo(entityPlayerMP, null);

        NBTTagCompound worldData = entityInfo.getWorldDataClipboard();

        if (worldData == null)
            throw ServerTranslations.commandException("commands.strucPaste.noClipboard");

        BlockPos pos = parameters.pos("x", "y", "z", commandSender.getPosition(), false).require();
        AxisAlignedTransform2D transform = parameters.transform("rotation", "mirror").optional().orElse(AxisAlignedTransform2D.ORIGINAL);
        String seed = parameters.get("seed").first().optional().orElse(null);
        boolean generate = parameters.has("generate");

        GenericStructure structureInfo = GenericStructure.createDefaultStructure();
        structureInfo.worldDataCompound = worldData;

        // TODO Generate with generation info?
        OperationRegistry.queueOperation(new OperationGenerateStructure(structureInfo, null, transform, pos, generate)
                .withSeed(seed)
                .prepare((WorldServer) commandSender.getEntityWorld()), commandSender);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return RCExpect.startRC()
                .pos("x", "y", "z")
                .named("rotation").rotation()
                .named("seed").randomString()
                .flag("mirror")
                .flag("generate")
                .get(server, sender, args, pos);
    }
}