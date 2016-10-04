/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.gui.editstructure;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiScreenModalTable;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.network.PacketSaveStructureHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructureInfo;
import ivorius.reccomplex.utils.SaveDirectoryData;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Created by lukas on 26.05.14.
 */
public class GuiEditGenericStructure extends GuiScreenModalTable
{
    TableDataSourceGenericStructure structureDataSource;

    public GuiEditGenericStructure(String key, GenericStructureInfo structureInfo, SaveDirectoryData data)
    {
        GuiTable structureProperties = new GuiTable(this, structureDataSource = new TableDataSourceGenericStructure(structureInfo, key, data, this, this));
        setTable(structureProperties);
    }

    @Override
    public void initGui()
    {
        if (currentTable() != null)
        {
            currentTable().setPropertiesBounds(Bounds.fromAxes(width / 2 - 155, 310, height / 2 - 110, 205));
        }
        super.initGui();

        if (tableStack().size() == 1)
        {
            buttonList.add(new GuiButton(1, width / 2 - 155, height / 2 + 95, 154, 20, IvTranslations.get("gui.cancel")));
            buttonList.add(new GuiButton(0, width / 2 + 1, height / 2 + 95, 154, 20, IvTranslations.get("reccomplex.gui.save")));
        }
        else
        {
            buttonList.add(new GuiButton(2, width / 2 - 155, height / 2 + 95, 310, 20, IvTranslations.get("gui.back")));
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException
    {
        if (keyCode != Keyboard.KEY_ESCAPE) // Prevent quitting without saving
        {
            super.keyTyped(keyChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        super.actionPerformed(button);

        if (button.id == 0)
        {
            PacketSaveStructureHandler.saveStructure(structureDataSource.getStructureInfo(), structureDataSource.getStructureKey(), structureDataSource.getSaveDirectoryData().getResult());
            this.mc.thePlayer.closeScreen();
        }
        else if (button.id == 1)
        {
            this.mc.thePlayer.closeScreen();
        }
        else if (button.id == 2)
        {
            popTable();
        }
    }
}
