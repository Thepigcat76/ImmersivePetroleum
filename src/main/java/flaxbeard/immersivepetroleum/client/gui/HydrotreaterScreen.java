package flaxbeard.immersivepetroleum.client.gui;

import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.resources.ResourceLocation;

public class HydrotreaterScreen /*extends IEContainerScreen<HydrotreaterContainer>*/{
	static final ResourceLocation GUI_TEXTURE = ResourceUtils.ip("textures/gui/hydrotreater.png");
	
	/*
	HydrotreaterTileEntity tile;
	public HydrotreaterScreen(HydrotreaterContainer inventorySlotsIn, Inventory inv, Component title){
		super(inventorySlotsIn, inv, title, GUI_TEXTURE);
		this.tile = this.menu.getTile();
		
		this.imageWidth = 140;
		this.imageHeight = 69;
	}
	
	@Override
	protected void renderLabels(PoseStack transform, int mouseX, int mouseY){
		// Render no labels
	}
	
	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas(){
		return List.of(
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_INPUT_A],
						new Rect2i(this.leftPos + 34, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_INPUT_B],
						new Rect2i(this.leftPos + 11, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new FluidInfoArea(
						this.tile.tanks[HydrotreaterTileEntity.TANK_OUTPUT],
						new Rect2i(this.leftPos + 92, this.topPos + 11, 16, 47),
						140, 0, 20, 51,
						GUI_TEXTURE
				),
				new EnergyInfoArea(leftPos + 122, topPos + 12, tile.energyStorage)
		);
	}
	*/
}
