package flaxbeard.immersivepetroleum.client.gui;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;

public class DistillationTowerScreen /*extends IEContainerScreen<DistillationTowerContainer>*/{
	static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/distillation.png");
	
	/*
	DistillationTowerTileEntity tile;
	
	public DistillationTowerScreen(DistillationTowerContainer container, Inventory playerInventory, Component title){
		super(container, playerInventory, title, GUI_TEXTURE);
		this.tile = container.getTile();
	}
	
	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY){
		// Render no labels
	}
	
	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(tile.tanks[0], new Rect2i(leftPos + 62, topPos + 21, 16, 47), 177, 31, 20, 51, GUI_TEXTURE),
				new EnergyInfoArea(leftPos + 158, topPos + 22, tile.energyStorage),
				new MultitankArea(new Rect2i(leftPos + 112, topPos + 21, 16, 47), tile.tanks[1].getCapacity(), () -> tile.tanks[1].fluids)
		);
	}
	*/
}
