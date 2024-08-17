package flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic;

import blusunrize.immersiveengineering.api.energy.AveragingEnergyStorage;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IClientTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.IServerTickableComponent;
import blusunrize.immersiveengineering.api.multiblocks.blocks.component.RedstoneControl.RSState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IInitialMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockContext;
import blusunrize.immersiveengineering.api.multiblocks.blocks.env.IMultiblockLevel;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockLogic;
import blusunrize.immersiveengineering.api.multiblocks.blocks.logic.IMultiblockState;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.MultiblockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.RelativeBlockFace;
import blusunrize.immersiveengineering.api.multiblocks.blocks.util.ShapeType;
import flaxbeard.immersivepetroleum.client.ClientProxy;
import flaxbeard.immersivepetroleum.client.gui.elements.PipeConfig;
import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.DerrickMultiblock;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.shapes.DerrickShape;
import flaxbeard.immersivepetroleum.common.blocks.stone.WellPipeBlock;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import flaxbeard.immersivepetroleum.common.cfg.IPServerConfig;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DerrickLogic implements IMultiblockLogic<DerrickLogic.State>, IServerTickableComponent<DerrickLogic.State>, IClientTickableComponent<DerrickLogic.State>{
	public static final int REQUIRED_WATER_AMOUNT = 125;
	public static final int REQUIRED_CONCRETE_AMOUNT = 125;
	
	public enum Inventory{
		/** Item Pipe Input */
		INPUT;
		
		public int id(){
			return ordinal();
		}
	}
	
	public static final FluidTank DUMMY_TANK = new FluidTank(0);
	
	/** Template-Location of the Fluid Input Port. (2 0 4)<br> */
	public static final BlockPos Fluid_IN = new BlockPos(2, 0, 4);
	
	/** Template-Location of the Fluid Output Port. (4 0 2)<br> */
	public static final BlockPos Fluid_OUT = new BlockPos(4, 0, 2);
	
	/** Template-Location of the Energy Input Ports.<br><pre>2 1 0</pre><br> */
	public static final MultiblockFace Energy_IN = new MultiblockFace(2, 1, 0, RelativeBlockFace.UP);
	
	/** Template-Location of the Redstone Input Port. (0 1 1)<br> */
	public static final BlockPos Redstone_IN = new BlockPos(0, 1, 1);
	
	@Override
	public State createInitialState(IInitialMultiblockContext<DerrickLogic.State> capabilitySource){
		return new DerrickLogic.State(capabilitySource);
	}
	
	/** Used as a list of blocks that should be used for the drill particle effect */
	private static final BlockState[] PARTICLESTATES = new BlockState[]{
			Blocks.STONE.defaultBlockState(),
			Blocks.GRANITE.defaultBlockState(),
			Blocks.GRAVEL.defaultBlockState(),
			Blocks.DEEPSLATE.defaultBlockState(),
			Blocks.DIORITE.defaultBlockState(),
			Blocks.SAND.defaultBlockState(),
			Blocks.ANDESITE.defaultBlockState(),
	};
	
	@Override
	public void tickClient(IMultiblockContext<DerrickLogic.State> context){
		final DerrickLogic.State derrickState = context.getState();
		final Level level = context.getLevel().getRawLevel();
		final BlockPos pos = derrickState.cache.getMasterWorldPos();
		
		if(derrickState.drilling){
			derrickState.rotation += 10;
			derrickState.rotation %= 2160; // 360 * 6
			
			double x = (pos.getX() + 0.5);
			double y = (pos.getY() + 1.0);
			double z = (pos.getZ() + 0.5);
			int r = level.random.nextInt(PARTICLESTATES.length);
			for(int i = 0;i < 5;i++){
				float xa = (level.random.nextFloat() - 0.5F) * 10.0F;
				float ya = 5.0F;
				float za = (level.random.nextFloat() - 0.5F) * 10.0F;
				
				level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, PARTICLESTATES[r]), x, y, z, xa, ya, za);
			}
		}
		
		if(derrickState.spilling){
			ClientProxy.spawnSpillParticles(level, pos, derrickState.fluidSpilled, 5, 1.25F, derrickState.clientFlow);
		}
	}
	
	@Override
	public void tickServer(IMultiblockContext<DerrickLogic.State> context){
		final DerrickLogic.State state = context.getState();
		final boolean rsEnabled = state.rsState.isEnabled(context);
		
		state.cache.updateCache(context.getLevel());
		
		final Level level = context.getLevel().getRawLevel();
		final BlockPos worldPosition = state.cache.getMasterWorldPos();
		
		boolean forceUpdate = false; // Should only be set to true if absolutely necessary
		boolean lastDrilling = state.drilling;
		boolean lastSpilling = state.spilling;
		state.drilling = state.spilling = false;
		
		if(worldPosition.getY() < level.getSeaLevel()){
			if(state.fluidSpilled == Fluids.EMPTY){
				state.fluidSpilled = Fluids.WATER;
			}
			state.spilling = true;
		}else{
			WellTileEntity well = state.createAndGetWell(state.getInventory(Inventory.INPUT) != ItemStack.EMPTY);
			if(rsEnabled){
				if(state.energy.extractEnergy(IPServerConfig.EXTRACTION.derrick_consumption.get(), true) >= IPServerConfig.EXTRACTION.derrick_consumption.get()){
					
					if(well != null){
						if(well.wellPipeLength < well.getMaxPipeLength()){
							if(well.pipes <= 0 && state.getInventory(Inventory.INPUT) != ItemStack.EMPTY){
								ItemStack stack = state.getInventory(Inventory.INPUT);
								if(stack.getCount() > 0){
									stack.shrink(1);
									well.pipes = WellTileEntity.PIPE_WORTH;
									
									if(stack.getCount() <= 0){
										state.setInventory(Inventory.INPUT, ItemStack.EMPTY);
									}
									
									well.setChanged();
									forceUpdate = true;
								}
							}
							
							if(well.pipes > 0){
								final BlockPos dPos = state.cache.getMasterWorldPos();
								final BlockPos wPos = well.getBlockPos();
								int realPipeLength = ((dPos.getY() - 1) - wPos.getY());
								
								if(well.phyiscalPipesList.size() < realPipeLength && well.wellPipeLength < realPipeLength){
									if(state.tank.drain(REQUIRED_CONCRETE_AMOUNT, IFluidHandler.FluidAction.SIMULATE).getAmount() >= REQUIRED_CONCRETE_AMOUNT){
										state.energy.extractEnergy(IPServerConfig.EXTRACTION.derrick_consumption.get(), false);
										
										if(state.advanceTimer()){
											Level world = context.getLevel().getRawLevel();
											int y = dPos.getY() - 1;
											for(;y > wPos.getY();y--){
												BlockPos current = new BlockPos(dPos.getX(), y, dPos.getZ());
												BlockState blockState = world.getBlockState(current);
												
												if(blockState.getBlock() == Blocks.BEDROCK || blockState.getBlock() == IPContent.Blocks.WELL.get()){
													break;
												}else if(!(blockState.getBlock() == IPContent.Blocks.WELL_PIPE.get() && !blockState.getValue(WellPipeBlock.BROKEN))){
													world.destroyBlock(current, false);
													world.setBlockAndUpdate(current, IPContent.Blocks.WELL_PIPE.get().defaultBlockState());
													
													well.phyiscalPipesList.add(y);
													
													state.tank.drain(REQUIRED_CONCRETE_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
													
													well.usePipe();
													break;
												}
											}
											
											if(well.phyiscalPipesList.size() >= realPipeLength && well.wellPipeLength >= realPipeLength){
												well.pastPhysicalPart = true;
												well.setChanged();
											}
										}
										
										forceUpdate = true;
										state.drilling = true;
									}
								}else{
									if(!state.tank.getFluid().isEmpty() && state.tank.getFluid().getFluid() == ExternalModContent.getIEFluid_Concrete()){
										// FIXME ! This happens every now and then, and i have not yet nailed down HOW this happens.
										// Void excess concrete.
										state.tank.drain(state.tank.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
										forceUpdate = true;
									}
									if(state.tank.drain(REQUIRED_WATER_AMOUNT, IFluidHandler.FluidAction.SIMULATE).getAmount() >= REQUIRED_WATER_AMOUNT){
										state.energy.extractEnergy(IPServerConfig.EXTRACTION.derrick_consumption.get(), false);
										
										if(state.advanceTimer()){
											state.restorePhysicalPipeProgress(well, dPos, realPipeLength);
											
											state.tank.drain(REQUIRED_WATER_AMOUNT, IFluidHandler.FluidAction.EXECUTE);
											well.usePipe();
										}
										
										forceUpdate = true;
										state.drilling = true;
									}
								}
							}
						}
					}
				}
			}
			
			if(well != null && well.wellPipeLength == well.getMaxPipeLength())
				state.outputReservoirFluid();
		}
		
		if(state.spilling && state.fluidSpilled == Fluids.EMPTY){
			state.fluidSpilled = IPContent.Fluids.CRUDEOIL.get();
		}
		if(!state.spilling && state.fluidSpilled != Fluids.EMPTY){
			state.fluidSpilled = Fluids.EMPTY;
		}
		
		if(forceUpdate || lastDrilling != state.drilling || lastSpilling != state.spilling){
			context.markMasterDirty();
		}
	}
	
	@Override
	public Function<BlockPos, VoxelShape> shapeGetter(ShapeType forType){
		return DerrickShape.GETTER;
	}
	
	public static class State implements IMultiblockState{
		private final LevelPosCache cache = new LevelPosCache();
		
		public final AveragingEnergyStorage energy = new AveragingEnergyStorage(16000);
		public final RSState rsState = RSState.enabledByDefault();
		
		public int timer = 0;
		public int rotation = 0;
		public boolean drilling;
		public boolean spilling;
		public final FluidTank tank = new FluidTank(8000, this::acceptsFluid);
		public final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
		
		/** Stores the current derrick configuration. */
		@Nullable
		public PipeConfig.Grid gridStorage;
		
		private Fluid fluidSpilled = Fluids.EMPTY;
		private int clientFlow;
		
		public State(IInitialMultiblockContext<State> context){
		}
		
		@Override
		public void readSaveNBT(CompoundTag nbt){
			this.tank.readFromNBT(nbt.getCompound("tank"));
			
			ContainerHelper.loadAllItems(nbt, this.inventory);
		}
		
		@Override
		public void writeSaveNBT(CompoundTag nbt){
			nbt.put("tank", this.tank.writeToNBT(new CompoundTag()));
			
			ContainerHelper.saveAllItems(nbt, this.inventory);
		}
		
		private void outputReservoirFluid(){
			
		}
		
		public ItemStack getInventory(Inventory inv){
			return this.inventory.get(inv.id());
		}
		
		public ItemStack setInventory(Inventory inv, ItemStack stack){
			return this.inventory.set(inv.id(), stack);
		}
		
		// Only accept as much Concrete and Water as needed
		private boolean acceptsFluid(FluidStack fs){
			if(!this.cache.bothPresent())
				return false;
			
			if(fs.isEmpty())
				return false;
			
			WellTileEntity well = createAndGetWell(false);
			if(well == null){
				return false;
			}
			
			final Fluid inFluid = fs.getFluid();
			final boolean isConcrete = inFluid == ExternalModContent.getIEFluid_Concrete();
			final boolean isWater = inFluid == Fluids.WATER;
			
			if(!isConcrete && !isWater)
				return false;
			
			int realPipeLength = (this.cache.getMasterWorldPos().getY() - 1) - well.getBlockPos().getY();
			int concreteNeeded = (REQUIRED_CONCRETE_AMOUNT * (realPipeLength - well.wellPipeLength));
			if(concreteNeeded > 0 && isConcrete){
				FluidStack tankFluidStack = this.tank.getFluid();
				
				if((!tankFluidStack.isEmpty() && inFluid != tankFluidStack.getFluid()) || tankFluidStack.getAmount() >= concreteNeeded){
					return false;
				}
				
				return concreteNeeded >= fs.getAmount();
			}
			
			if(concreteNeeded <= 0){
				int waterNeeded = REQUIRED_WATER_AMOUNT * (well.getMaxPipeLength() - well.wellPipeLength);
				if(waterNeeded > 0 && isWater){
					FluidStack tankFluidStack = this.tank.getFluid();
					
					if((!tankFluidStack.isEmpty() && inFluid != tankFluidStack.getFluid()) || tankFluidStack.getAmount() >= waterNeeded){
						return false;
					}
					
					return waterNeeded >= fs.getAmount();
				}
			}
			
			return false;
		}
		
		/** Only returns true if the timer reached zero */
		private boolean advanceTimer(){
			if(this.timer-- <= 0){
				this.timer = 10;
				return true;
			}
			return false;
		}
		
		private WellTileEntity wellCache = null;
		/**
		 * Create and Get the {@link WellTileEntity}.
		 *
		 * @param popList Set to true, to try and populate the {@link WellTileEntity#tappedIslands} list.
		 * @return WellTileEntity or possibly null
		 */
		@Nullable
		public WellTileEntity createAndGetWell(boolean popList){
			if(this.wellCache != null && this.wellCache.isRemoved()){
				this.wellCache = null;
			}
			
			final Level world = this.cache.getMasterLevel();
			final BlockPos pos = this.cache.getMasterWorldPos();
			
			if(this.wellCache == null){
				WellTileEntity well = null;
				
				for(int y = pos.getY() - 1;y >= world.getMinBuildHeight();y--){
					BlockPos current = new BlockPos(pos.getX(), y, pos.getZ());
					BlockState state = world.getBlockState(current);
					
					if(state.getBlock() == IPContent.Blocks.WELL.get()){
						well = (WellTileEntity) world.getBlockEntity(current);
						break;
					}else if(state.getBlock() == Blocks.BEDROCK){
						world.setBlockAndUpdate(current, IPContent.Blocks.WELL.get().defaultBlockState());
						well = (WellTileEntity) world.getBlockEntity(current);
						break;
					}
				}
				
				this.wellCache = well;
			}
			
			if(popList && this.wellCache != null && this.wellCache.tappedIslands.isEmpty()){
				PipeConfig.Grid grid = this.gridStorage;
				
				if(grid != null){
					transferGridDataToWell(this.wellCache);
				}else{
					this.wellCache.tappedIslands.add(Utils.toColumnPos(pos));
					this.wellCache.setChanged();
				}
			}
			
			if(this.wellCache != null){
				this.wellCache.abortSelfDestructSequence();
			}
			
			return this.wellCache;
		}
		
		/** Only gets the well if it exists, does not attempt to create it. May return null. */
		@Nullable
		public WellTileEntity getWell(BlockPos pos){
			if(this.wellCache != null && this.wellCache.isRemoved()){
				this.wellCache = null;
			}
			
			if(this.wellCache == null){
				Level level = this.cache.getMasterLevel();
				WellTileEntity well = null;
				
				for(int y = pos.getY() - 1;y >= level.getMinBuildHeight();y--){
					BlockPos current = new BlockPos(pos.getX(), y, pos.getZ());
					BlockState state = level.getBlockState(current);
					
					if(state.getBlock() == IPContent.Blocks.WELL.get()){
						well = (WellTileEntity) level.getBlockEntity(current);
						break;
					}
				}
				
				this.wellCache = well;
			}
			
			return this.wellCache;
		}
		
		public void transferGridDataToWell(@Nullable WellTileEntity well){
			if(well != null && this.gridStorage != null){
				final BlockPos wPos = this.cache.getMasterWorldPos();
				
				int additionalPipes = 0;
				List<ColumnPos> list = new ArrayList<>();
				PipeConfig.Grid grid = this.gridStorage;
				
				for(int j = 0;j < grid.getHeight();j++){
					for(int i = 0;i < grid.getWidth();i++){
						int type = grid.get(i, j);
						
						if(type > 0){
							switch(type){
								case PipeConfig.PIPE_PERFORATED:
								case PipeConfig.PIPE_PERFORATED_FIXED:{
									int x = i - (grid.getWidth() / 2);
									int z = j - (grid.getHeight() / 2);
									ColumnPos pos = new ColumnPos(wPos.getX() + x, wPos.getZ() + z);
									list.add(pos);
								}
								case PipeConfig.PIPE_NORMAL:{
									additionalPipes++;
								}
							}
						}
					}
				}
				
				well.tappedIslands = list;
				well.additionalPipes = additionalPipes;
				well.setChanged();
			}
		}
		
		public void restorePhysicalPipeProgress(@Nonnull WellTileEntity well, BlockPos dPos, int realPipeLength){
			int min = Math.min(well.wellPipeLength, realPipeLength);
			for(int i = 1;i < min;i++){
				BlockPos current = new BlockPos(dPos.getX(), dPos.getY() - i, dPos.getZ());
				BlockState state = this.cache.getMasterLevel().getBlockState(current);
				if(!(state.getBlock() instanceof WellPipeBlock)){
					this.cache.getMasterLevel().destroyBlock(current, false);
					this.cache.getMasterLevel().setBlockAndUpdate(current, IPContent.Blocks.WELL_PIPE.get().defaultBlockState());
				}
			}
		}
	}
	
	/** Very dirty hack, but honestly what else can I do? */
	private static class LevelPosCache{
		private Level masterLevelCache;
		private BlockPos masterPosCache;
		private LevelPosCache(){}
		
		public void updateCache(IMultiblockLevel mbLevel){
			final Level level = mbLevel.getRawLevel();
			final BlockPos pos = mbLevel.toAbsolute(DerrickMultiblock.INSTANCE.getMasterFromOriginOffset());
			
			if(level != null && this.masterLevelCache != level){
				this.masterLevelCache = level;
			}
			
			if(pos != null && this.masterPosCache != pos){
				this.masterPosCache = pos;
			}
		}
		
		public boolean bothPresent(){
			return this.masterLevelCache != null && this.masterPosCache != null;
		}
		
		public Level getMasterLevel(){
			return this.masterLevelCache;
		}
		
		public BlockPos getMasterWorldPos(){
			return this.masterPosCache;
		}
	}
}
