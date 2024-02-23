package flaxbeard.immersivepetroleum.client.gui.elements;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import flaxbeard.immersivepetroleum.client.gui.CokerUnitScreen;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

public class CokerChamberInfoArea extends InfoArea{
	private final CokerUnitTileEntity.CokingChamber chamber;
	private final FluidInfoArea fluidDisplay;
	
	public CokerChamberInfoArea(CokerUnitTileEntity.CokingChamber chamber, Rect2i area){
		super(area);
		this.chamber = chamber;
		this.fluidDisplay = new FluidInfoArea(
				chamber.getTank(),
				new Rect2i(area.getX(), area.getY(), 6, 38),
				0, 0, 0, 0,
				CokerUnitScreen.GUI_TEXTURE
		);
	}
	
	@Override
	protected void fillTooltipOverArea(int mouseX, int mouseY, List<Component> tooltip){
		fluidDisplay.fillTooltipOverArea(mouseX, mouseY, tooltip);
	}
	
	@Override
	public void draw(PoseStack transform){
		MCUtil.bindTexture(CokerUnitScreen.GUI_TEXTURE);
		int scale = 38;
		int off = (int) (chamber.getTotalAmount() / (float) chamber.getCapacity() * scale);
		this.blit(transform, area.getX(), area.getY() + scale - off, 200, 51, 6, off);
		
		// Vertical Overlay to visualize progress
		off = (int) (chamber.getTotalAmount() > 0 ? scale * (chamber.getOutputAmount() / (float) chamber.getCapacity()) : 0);
		this.blit(transform, area.getX(), area.getY() + scale - off, 206, 51 + (scale - off), 6, off);
		fluidDisplay.draw(transform);
	}
}
