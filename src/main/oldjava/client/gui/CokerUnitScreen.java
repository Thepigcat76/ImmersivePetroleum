package flaxbeard.immersivepetroleum.client.gui;

import java.util.List;

import javax.annotation.Nonnull;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import flaxbeard.immersivepetroleum.client.gui.elements.CokerChamberInfoArea;
import flaxbeard.immersivepetroleum.client.gui.elements.EnergyDisplay;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.CokerUnitLogic;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CokerUnitScreen extends IEContainerScreen<CokerUnitContainer>{
	public static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/coker.png");
	
	CokerUnitLogic.State tile;
	public CokerUnitScreen(CokerUnitContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title, GUI_TEXTURE);
		this.tile = menu.getTile();
		
		this.imageWidth = 200;
		this.imageHeight = 187;
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY){
		// Render no labels
	}
	
	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(
						this.tile.bufferTanks.input(),
						new Rect2i(this.leftPos + 32, this.topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.bufferTanks.output(),
						new Rect2i(this.leftPos + 152, this.topPos + 14, 16, 47),
						202, 2, 16, 47,
						GUI_TEXTURE
				),
				new EnergyDisplay(this.leftPos + 168, this.topPos + 67, 7, 21, this.tile.energy),
				new CokerChamberInfoArea(this.tile.chambers.primary(), new Rect2i(this.leftPos + 74, this.topPos + 24, 6, 38)),
				new CokerChamberInfoArea(this.tile.chambers.secondary(), new Rect2i(this.leftPos + 120, this.topPos + 24, 6, 38))
		);
	}
}
