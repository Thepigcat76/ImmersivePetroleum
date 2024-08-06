package flaxbeard.immersivepetroleum.client.render.debugging;

import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockBEHelper;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockBE;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.ComponentInstance;
import blusunrize.immersiveengineering.common.blocks.multiblocks.blockimpl.MultiblockBEHelperMaster;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import flaxbeard.immersivepetroleum.api.reservoir.AxisAlignedIslandBB;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import flaxbeard.immersivepetroleum.client.utils.MCUtil;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage.RegionData;
import flaxbeard.immersivepetroleum.common.ReservoirRegionDataStorage.RegionPos;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.CokerUnitLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.HydroTreaterLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.OilTankLogic;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.FlarestackTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.IPTileEntityBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.items.DebugItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DebugRenderHandler{
	public DebugRenderHandler(){
	}
	
	private boolean isHoldingDebugItem(Player player){
		ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
		
		return (main != ItemStack.EMPTY && main.getItem() == IPContent.DEBUGITEM.get()) || (off != ItemStack.EMPTY && off.getItem() == IPContent.DEBUGITEM.get());
	}
	
	@SubscribeEvent
	public void renderDebuggingOverlay(RenderGuiOverlayEvent.Post event){
		Minecraft mc = Minecraft.getInstance();
		
		if(mc.player != null && event.getOverlay().id() == VanillaGuiOverlay.DEBUG_SCREEN.id()){
			Player player = mc.player;
			
			if(isHoldingDebugItem(player)){
				HitResult rt = mc.hitResult;
				if(rt != null){
					switch(rt.getType()){
						case BLOCK -> {
							BlockHitResult result = (BlockHitResult) rt;
							Level world = player.level();
							
							BlockState blockState = world.getBlockState(result.getBlockPos());
							
							List<Component> debugOut = new ArrayList<>();
							
							if(blockState.getBlock() instanceof EntityBlock){
								final BlockEntity te = world.getBlockEntity(result.getBlockPos());
								
								if(te instanceof GasGeneratorTileEntity gas){
									debugOut.add(toTranslation(te.getBlockState().getBlock().getDescriptionId()).withStyle(ChatFormatting.GOLD));
									
								}else if(te instanceof IPTileEntityBase){
									debugOut.add(toTranslation(te.getBlockState().getBlock().getDescriptionId()).withStyle(ChatFormatting.GOLD));
									
									if(te instanceof AutoLubricatorTileEntity autolube){
										FluidTank tank = autolube.tank;
										FluidStack fs = tank.getFluid();
										
										debugOut.add(toText("isSlave").withStyle(autolube.isSlave ? ChatFormatting.GREEN : ChatFormatting.RED));
										if(!autolube.isSlave){
											debugOut.add(toText("Facing: " + autolube.facing.getName()));
											debugOut.add(toText("Tank: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
										}
										
									}else if(te instanceof FlarestackTileEntity flare){
									}else if(te instanceof WellTileEntity well){
									}else if(te instanceof WellPipeTileEntity wellPipe){
									}
									
								}else if(te instanceof IMultiblockBE<?> generic){
									IMultiblockState mbState;
									BlockPos tPos;
									{
										IMultiblockBEHelper<? extends IMultiblockState> helper = generic.getHelper();
										tPos = helper.getPositionInMB();
										
										BlockEntity masterMaybe = helper.getContext().getLevel().getBlockEntity(helper.getMultiblock().masterPosInMB());
										if(masterMaybe instanceof IMultiblockBE<?> master){
											generic = master;
											helper = master.getHelper();
										}
										
										mbState = helper.getState();
										
										Block block = helper.getMultiblock().block().get();
										
										debugOut.add(toText("Template XYZ: " + tPos.getX() + ", " + tPos.getY() + ", " + tPos.getZ()));
										
										MutableComponent name = toTranslation(block.getDescriptionId()).withStyle(ChatFormatting.GOLD);
										
										if(helper instanceof MultiblockBEHelperMaster<? extends IMultiblockState> masterHelper){
											IMultiblockContext<?> context = masterHelper.getComponentInstances().stream()
													.filter(c -> c.component() instanceof RedstoneControl<?>)
													.map(ComponentInstance::wrappedContext)
													.findAny().orElse(null);
											
											if(context != null){
												RSState rsstate = ((RSState) context.getState());
												
												name.append(toText((!rsstate.isEnabled(context)) ? " (Redstoned)" : "").withStyle(ChatFormatting.RED));
											}
										}
										
										/*// TODO Debug Display: Enery Storage
										if(generic instanceof PoweredMultiblockBlockEntity<?, ?> poweredGeneric){
											name.append(toText(poweredGeneric.shouldRenderAsActive() ? " (Active)" : "").withStyle(ChatFormatting.GREEN));
											debugOut.add(toText(poweredGeneric.energyStorage.getEnergyStored() + "/" + poweredGeneric.energyStorage.getMaxEnergyStored() + "RF"));
										}
										*/
										
										/*// TODO Debug Display: Lubrication
										synchronized(LubricatedHandler.lubricatedTiles){
											for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
												if(info.pos.equals(generic.getBlockPos())){
													name.append(toText(" (Lubricated " + info.ticks + ")").withStyle(ChatFormatting.YELLOW));
												}
											}
										}
										*/
										
										debugOut.add(name);
									}
									
									if(mbState instanceof DistillationTowerLogic.State towerState){
										distillationtower(debugOut, towerState);
										
									}else if(mbState instanceof CokerUnitLogic.State coker){
										cokerunit(debugOut, coker);
										
									}else if(mbState instanceof HydroTreaterLogic.State treater){
										hydrotreater(debugOut, treater);
										
									}else if(mbState instanceof OilTankLogic.State oiltank){
										oiltank(debugOut, oiltank, tPos);
										
									}else if(mbState instanceof DerrickLogic.State derrick){
										derrick(debugOut, derrick);
									}
								}
							}else{
								if(blockState.getBlock() instanceof RedStoneWireBlock){
									debugOut.add(toText("Redstone Wire").withStyle(ChatFormatting.GOLD));
									debugOut.add(toText("Power: " + blockState.getValue(RedStoneWireBlock.POWER)));
								}
							}
							
							if(!debugOut.isEmpty()){
								BlockPos hit = result.getBlockPos();
								debugOut.add(0, toText("World XYZ: " + hit.getX() + ", " + hit.getY() + ", " + hit.getZ()));
								
								renderOverlay(event.getGuiGraphics(), debugOut);
							}
						}
						case ENTITY -> {
							EntityHitResult result = (EntityHitResult) rt;
							
							if(result.getEntity() instanceof MotorboatEntity boat){
								
								List<Component> debugOut = new ArrayList<>();
								
								debugOut.add(toText("").append(boat.getDisplayName()).withStyle(ChatFormatting.GOLD));
								
								FluidStack fluid = boat.getContainedFluid();
								if(fluid == FluidStack.EMPTY){
									debugOut.add(toText("Tank: Empty"));
								}else{
									debugOut.add(toText("Tank: " + fluid.getAmount() + "/" + boat.getMaxFuel() + "mB of ").append(fluid.getDisplayName()));
								}
								
								NonNullList<ItemStack> upgrades = boat.getUpgrades();
								int i = 0;
								for(ItemStack upgrade:upgrades){
									if(upgrade == null || upgrade == ItemStack.EMPTY){
										debugOut.add(toText("Upgrade " + (++i) + ": Empty"));
									}else{
										debugOut.add(toText("Upgrade " + (++i) + ": ").append(upgrade.getHoverName()));
									}
								}
								
								renderOverlay(event.getGuiGraphics(), debugOut);
							}
						}
						default -> {
							boolean debug = false;
							if(debug){
								final ReservoirRegionDataStorage storage = ReservoirRegionDataStorage.get();
								BlockPos playerPos = MCUtil.getPlayer().blockPosition();
								
								RegionPos rLocal = new RegionPos(playerPos);
								
								RegionPos r0 = new RegionPos(playerPos, 1, -1);
								RegionPos r1 = new RegionPos(playerPos, 1, 1);
								RegionPos r2 = new RegionPos(playerPos, -1, -1);
								RegionPos r3 = new RegionPos(playerPos, -1, 1);
								
								boolean bLocal = storage.getRegionData(rLocal) != null;
								
								boolean b0 = storage.getRegionData(r0) != null;
								boolean b1 = storage.getRegionData(r1) != null;
								boolean b2 = storage.getRegionData(r2) != null;
								boolean b3 = storage.getRegionData(r3) != null;
								
								List<Component> list = List.of(
									Component.literal(String.format("PlayerXYZ: %d %d %d", playerPos.getX(), playerPos.getY(), playerPos.getZ())),
									Component.literal(String.format("LocalXZ: %d %d", rLocal.x(), rLocal.z())).withStyle(bLocal ? ChatFormatting.GREEN : ChatFormatting.RED),
									Component.literal(String.format("XZ: %d %d", r0.x(), r0.z())).withStyle(b0 ? ChatFormatting.GREEN : ChatFormatting.RED),
									Component.literal(String.format("XZ: %d %d", r1.x(), r1.z())).withStyle(b1 ? ChatFormatting.GREEN : ChatFormatting.RED),
									Component.literal(String.format("XZ: %d %d", r2.x(), r2.z())).withStyle(b2 ? ChatFormatting.GREEN : ChatFormatting.RED),
									Component.literal(String.format("XZ: %d %d", r3.x(), r3.z())).withStyle(b3 ? ChatFormatting.GREEN : ChatFormatting.RED)
								);
								renderOverlay(event.getGuiGraphics(), list);
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void renderLevelStage(RenderLevelStageEvent event){
		if(event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS){
			reservoirDebuggingRender(event);
		}
	}
	
	private void reservoirDebuggingRender(RenderLevelStageEvent event){
		if(ReservoirHandler.getGenerator() == null){
			return;
		}
		
		Player player = MCUtil.getPlayer();
		
		ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
		ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
		
		if((main != ItemStack.EMPTY && main.getItem() == IPContent.DEBUGITEM.get()) || (off != ItemStack.EMPTY && off.getItem() == IPContent.DEBUGITEM.get())){
			DebugItem.Modes mode = null;
			if(main != ItemStack.EMPTY){
				mode = DebugItem.getMode(main);
			}
			if(off != ItemStack.EMPTY){
				mode = DebugItem.getMode(off);
			}
			
			if(mode == DebugItem.Modes.SEEDBASED_RESERVOIR || mode == DebugItem.Modes.SEEDBASED_RESERVOIR_AREA_TEST){
				PoseStack matrix = event.getPoseStack();
				Level world = player.getCommandSenderWorld();
				BlockPos playerPos = player.blockPosition();
				
				matrix.pushPose();
				{
					// Anti-Jiggle when moving
					Vec3 renderView = MCUtil.getGameRenderer().getMainCamera().getPosition();
					matrix.translate(-renderView.x, -renderView.y, -renderView.z);
					
					matrix.pushPose();
					{
						MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
						
						int radius = 12;
						for(int i = -radius;i <= radius;i++){
							for(int j = -radius;j <= radius;j++){
								ChunkPos cPos = new ChunkPos(playerPos.offset(16 * i, 0, 16 * j));
								int chunkX = cPos.getMinBlockX();
								int chunkZ = cPos.getMinBlockZ();
								
								for(int cX = 0;cX < 16;cX++){
									for(int cZ = 0;cZ < 16;cZ++){
										int x = chunkX + cX;
										int z = chunkZ + cZ;
										
										matrix.pushPose();
										{
											double n = ReservoirHandler.getValueOf(world, x, z);
											if(n > -1){
												int c = (int) Math.round(9 * n);
												
												DyeColor color = switch(c){
													case 1 -> DyeColor.BLUE;
													case 2 -> DyeColor.CYAN;
													case 3 -> DyeColor.GREEN;
													case 4 -> DyeColor.LIME;
													case 5 -> DyeColor.YELLOW;
													case 6 -> DyeColor.ORANGE;
													case 7 -> DyeColor.RED;
													default -> c > 7 ? DyeColor.WHITE : DyeColor.BLACK;
												};
												
												int r = (color.getTextColor() & 0xFF0000) >> 16;
												int g = (color.getTextColor() & 0x00FF00) >> 8;
												int b = (color.getTextColor() & 0x0000FF);
												
												int height = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, 0, z)).getY();
												for(;height > 0;height--){
													if(world.getBlockState(new BlockPos(x, height - 1, z)).isSolidRender(world, new BlockPos(x, height - 1, z))){
														break;
													}
												}
												
												matrix.translate(x, Math.max(63, height) + 0.0625, z);
												
												Matrix4f mat = matrix.last().pose();
												
												VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_POSITION_COLOR);
												builder.vertex(mat, 0, 0, 0).color(r, g, b, 127).endVertex();
												builder.vertex(mat, 0, 0, 1).color(r, g, b, 127).endVertex();
												builder.vertex(mat, 1, 0, 1).color(r, g, b, 127).endVertex();
												builder.vertex(mat, 1, 0, 0).color(r, g, b, 127).endVertex();
											}
										}
										matrix.popPose();
									}
								}
							}
						}
						buffer.endBatch();
					}
					matrix.popPose();
					
					matrix.pushPose();
					{
						ReservoirRegionDataStorage storage = ReservoirRegionDataStorage.get();
						final ResourceKey<Level> dimKey = player.getCommandSenderWorld().dimension();
						final Set<ReservoirIsland> islands = new HashSet<>();
						
						RegionPos pLocal = new RegionPos(playerPos);
						RegionPos p0 = new RegionPos(playerPos, 1, -1);
						RegionPos p1 = new RegionPos(playerPos, 1, 1);
						RegionPos p2 = new RegionPos(playerPos, -1, -1);
						RegionPos p3 = new RegionPos(playerPos, -1, 1);
						
						RegionData rLocal = storage.getRegionData(pLocal);
						RegionData r0 = storage.getRegionData(p0);
						RegionData r1 = storage.getRegionData(p1);
						RegionData r2 = storage.getRegionData(p2);
						RegionData r3 = storage.getRegionData(p3);
						
						RegionData[] array = {rLocal, r0, r1, r2, r3};
						for(RegionData rd:array){
							if(rd != null){
								Multimap<ResourceKey<Level>, ReservoirIsland> m = rd.getReservoirIslandList();
								synchronized(m){
									islands.addAll(m.get(dimKey));
								}
							}
						}
						
						{
							MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
							
							if(islands != null && !islands.isEmpty()){
								float y = 128.0625F;
								int radius = 128;
								radius = radius * radius + radius * radius;
								for(ReservoirIsland island:islands){
									BlockPos center = island.getBoundingBox().getCenter();
									
									if(center.distSqr(playerPos) <= radius){
										AxisAlignedIslandBB bounds = island.getBoundingBox();
										matrix.pushPose();
										{
											float minX = bounds.minX() + 0.5F;
											float minZ = bounds.minZ() + 0.5F;
											float maxX = bounds.maxX() + 0.5F;
											float maxZ = bounds.maxZ() + 0.5F;
											
											VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_LINE);
											
											Matrix4f mat = matrix.last().pose();
											Matrix3f nor = matrix.last().normal();
											
											builder.vertex(mat, minX, y, minZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, maxX, y, minZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, minX, y, maxZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, maxX, y, maxZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, minX, y, minZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, minX, y, maxZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, maxX, y, minZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
											builder.vertex(mat, maxX, y, maxZ).color(255, 0, 255, 127).normal(nor, 0, 1, 0).endVertex();
										}
										matrix.popPose();
										
										if(island.getPolygon() != null && !island.getPolygon().isEmpty()){
											List<ColumnPos> poly = island.getPolygon();
											
											matrix.pushPose();
											{
												VertexConsumer builder = buffer.getBuffer(IPRenderTypes.TRANSLUCENT_LINE);
												Matrix4f mat = matrix.last().pose();
												Matrix3f nor = matrix.last().normal();
												
												// Draw polygon as line
												int j = poly.size() - 1;
												for(int i = 0;i < poly.size();i++){
													ColumnPos a = poly.get(j);
													ColumnPos b = poly.get(i);
													float f = i / (float) poly.size();
													
													builder.vertex(mat, a.x() + .5F, y, a.z() + .5F).color(f, 0.0F, 1 - f, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													builder.vertex(mat, b.x() + .5F, y, b.z() + .5F).color(f, 0.0F, 1 - f, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													
													j = i;
												}
												
												// Center Marker
												{
													// Y
													builder.vertex(mat, center.getX() + .5F, 128F, center.getZ() + .5F).color(0.0F, 1.0F, 0.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													builder.vertex(mat, center.getX() + .5F, 129F, center.getZ() + .5F).color(0.0F, 1.0F, 0.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													
													// X
													builder.vertex(mat, center.getX(), 128.5F, center.getZ() + .5F).color(1.0F, 0.0F, 0.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													builder.vertex(mat, center.getX() + 1, 128.5F, center.getZ() + .5F).color(1.0F, 0.0F, 0.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													
													// Z
													builder.vertex(mat, center.getX() + .5F, 128.5F, center.getZ()).color(0.0F, 0.0F, 1.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
													builder.vertex(mat, center.getX() + .5F, 128.5F, center.getZ() + 1).color(0.0F, 0.0F, 1.0F, 0.5F).normal(nor, 0F, 1F, 0F).endVertex();
												}
											}
											matrix.popPose();
										}
									}
								}
							}
							
							buffer.endBatch();
						}
					}
					matrix.popPose();
				}
				matrix.popPose();
				
			}
		}
	}
	
	private static void renderOverlay(GuiGraphics graphics, List<Component> debugOut){
		Font font = MCUtil.getFont();
		
		PoseStack matrix = graphics.pose();
		
		matrix.pushPose();
		{
			MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			for(int i = 0;i < debugOut.size();i++){
				int w = font.width(debugOut.get(i).getString());
				int yOff = i * (font.lineHeight + 2);
				
				matrix.pushPose();
				{
					matrix.translate(0, 0, 1);
					GuiHelper.drawColouredRect(1, 1 + yOff, w + 1, 10, 0xAF_000000, buffer, matrix);
					buffer.endBatch();
					// Draw string without shadow
					graphics.drawString(font, debugOut.get(i), 2, 2 + yOff, -1);
				}
				matrix.popPose();
			}
		}
		matrix.popPose();
	}
	
	private static void distillationtower(List<Component> text, DistillationTowerLogic.State tower){
		text.add(toText("Input Tank").withStyle(ChatFormatting.UNDERLINE));
		{
			List<FluidStack> fluids = tower.tanks.input().fluids;
			if(!fluids.isEmpty()){
				for(FluidStack fstack : fluids){
					text.add(toText("  " + fstack.getDisplayName().getString() + " (" + fstack.getAmount() + "mB)"));
				}
			}else{
				text.add(toText("  Empty"));
			}
		}
		
		text.add(toText("Output Tank").withStyle(ChatFormatting.UNDERLINE));
		{
			List<FluidStack> fluids = tower.tanks.output().fluids;
			if(!fluids.isEmpty()){
				for(FluidStack fstack : fluids){
					text.add(toText("  " + fstack.getDisplayName().getString() + " (" + fstack.getAmount() + "mB)"));
				}
			}else{
				text.add(toText("  Empty"));
			}
		}
	}
	
	private static void cokerunit(List<Component> text, CokerUnitLogic.State coker){
		{
			FluidTank tank = coker.bufferTanks.input();
			FluidStack fs = tank.getFluid();
			text.add(toText("In Buffer: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
		}
		
		{
			FluidTank tank = coker.bufferTanks.output();
			FluidStack fs = tank.getFluid();
			text.add(toText("Out Buffer: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
		}
		
		chamberInfo(text, coker.chambers.primary(), 0);
		chamberInfo(text, coker.chambers.secondary(), 1);
	}
	
	private static void chamberInfo(List<Component> text, CokerUnitLogic.CokingChamber chamber, int id){
		FluidTank tank = chamber.getTank();
		FluidStack fs = tank.getFluid();
		
		float completed = chamber.getTotalAmount() > 0 ? 100 * (chamber.getOutputAmount() / (float) chamber.getTotalAmount()) : 0;
		
		text.add(toText("Chamber " + id).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA));
		text.add(toText("State: " + chamber.getState().toString()));
		text.add(toText("  Tank: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB " + (fs.isEmpty() ? "" : "(" + fs.getDisplayName().getString() + ")"))));
		text.add(toText("  Content: " + chamber.getTotalAmount() + " / " + chamber.getCapacity()).append(" (" + chamber.getInputItem().getHoverName().getString() + ")"));
		text.add(toText("  Out: " + chamber.getOutputItem().getHoverName().getString()));
		text.add(toText("  " + Mth.floor(completed) + "% Completed. (Raw: " + completed + ")"));
	}
	
	private static void hydrotreater(List<Component> text, HydroTreaterLogic.State treater){
		treater.tanks.primary();
		treater.tanks.secondary();
		treater.tanks.output();
		
		tankDisplay(text, treater.tanks.primary());
		tankDisplay(text, treater.tanks.secondary());
		tankDisplay(text, treater.tanks.output());
	}
	
	private static void tankDisplay(List<Component> text, IFluidTank tank){
		FluidStack fs = tank.getFluid();
		
		String str = "Tank: " + (fs.getAmount() + "/" + tank.getCapacity() + "mB");
		if(!fs.isEmpty()){
			str += " (" + fs.getDisplayName().getString() + ")";
		}
		
		text.add(toText(str));
	}
	
	private static void oiltank(List<Component> text, OilTankLogic.State tank, BlockPos posInMultiblock){
		OilTankLogic.Port port = null;
		for(OilTankLogic.Port p:OilTankLogic.Port.values()){
			if(p.matches(posInMultiblock)){
				port = p;
				break;
			}
		}
		
		if(port != null){
			OilTankLogic.PortState portState = tank.portConfig.get(port);
			boolean isInput = portState == OilTankLogic.PortState.INPUT;
			text.add(toText("Port: ")
					.append(toText(port.getSerializedName()))
					.append(toText(" " + portState.getSerializedName())
							.withStyle(isInput ? ChatFormatting.AQUA : ChatFormatting.GOLD)));
		}
		
		tankDisplay(text, tank.tank);
	}
	
	private static void derrick(List<Component> text, DerrickLogic.State derrick){
		tankDisplay(text, derrick.tank);
	}
	
	static MutableComponent toText(String string){
		return Component.literal(string);
	}
	
	static MutableComponent toTranslation(String translationKey, Object... args){
		return Component.translatable(translationKey, args);
	}
}
