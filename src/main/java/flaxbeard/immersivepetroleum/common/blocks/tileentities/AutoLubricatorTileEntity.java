package flaxbeard.immersivepetroleum.common.blocks.tileentities;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.ILubricationHandler;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.interfaces.IBlockEntityDrop;
import flaxbeard.immersivepetroleum.common.blocks.interfaces.IPlacementReader;
import flaxbeard.immersivepetroleum.common.blocks.interfaces.IPlayerInteraction;
import flaxbeard.immersivepetroleum.common.blocks.ticking.IPCommonTickableTile;
import flaxbeard.immersivepetroleum.common.blocks.wooden.AutoLubricatorBlock;
import flaxbeard.immersivepetroleum.common.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class AutoLubricatorTileEntity extends IPTileEntityBase implements IPCommonTickableTile, IPlacementReader, IPlayerInteraction, IBlockEntityDrop, IEBlockInterfaces.IBlockOverlayText{
	public boolean isSlave;
	public Direction facing = Direction.NORTH;
	public FluidTank tank = new FluidTank(8000, fluid -> (fluid != null && LubricantHandler.isValidLube(fluid.getFluid())));
	
	public AutoLubricatorTileEntity(BlockPos pWorldPosition, BlockState pBlockState){
		super(IPTileTypes.AUTOLUBE.get(), pWorldPosition, pBlockState);
	}
	
	public AutoLubricatorTileEntity master(){
		if(this.isSlave){
			BlockEntity te = getWorldNonnull().getBlockEntity(getBlockPos().below());
			if(te instanceof AutoLubricatorTileEntity autolube){
				return autolube;
			}else{
				return null;
			}
		}else{
			return this;
		}
	}
	
	@Override
	protected void readCustom(CompoundTag compound){
		this.isSlave = compound.getBoolean("slave");
		
		Direction facing = Direction.byName(compound.getString("facing"));
		if(facing.get2DDataValue() == -1)
			this.facing = Direction.NORTH;
		this.facing = facing;
		
		this.tank.readFromNBT(compound.getCompound("tank"));
	}
	
	@Override
	protected void writeCustom(CompoundTag compound){
		compound.putBoolean("slave", this.isSlave);
		compound.putString("facing", this.facing.getName());
		compound.putInt("count", this.count);
		
		CompoundTag tank = this.tank.writeToNBT(new CompoundTag());
		compound.put("tank", tank);
	}
	
	public void readTank(CompoundTag nbt){
		this.tank.readFromNBT(nbt.getCompound("tank"));
	}
	
	public void writeTank(CompoundTag nbt, boolean toItem){
		boolean write = this.tank.getFluidAmount() > 0;
		CompoundTag tankTag = this.tank.writeToNBT(new CompoundTag());
		if(!toItem || write)
			nbt.put("tank", tankTag);
	}
	
	@Override
	public void readOnPlacement(LivingEntity placer, ItemStack stack){
		if(stack.hasTag()){
			readTank(stack.getTag());
		}
		
		if(placer instanceof Player player){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null && handler.isPlacedCorrectly(this.level, this, this.facing) != null){
				Utils.unlockIPAdvancement(player, "main/auto_lubricator");
			}
		}
	}
	
	@Override
	@Nonnull
	public List<ItemStack> getBlockEntityDrop(LootContext context){
		BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
		if(state.getValue(AutoLubricatorBlock.SLAVE)){
			return List.of(ItemStack.EMPTY);
		}
		
		ItemStack stack = new ItemStack(state.getBlock());
		
		BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if(te instanceof AutoLubricatorTileEntity autolube){
			CompoundTag tag = new CompoundTag();
			autolube.writeTank(tag, true);
			if(!tag.isEmpty()){
				stack.setTag(tag);
			}
		}
		
		return List.of(stack);
	}
	
	IFluidHandler outputHandler;
	public @Nullable IFluidHandler getCapability(Direction side){
		if(this.isSlave && (side == null || side == Direction.UP)){
			AutoLubricatorTileEntity master = master();
			
			if(master == null)
				return null;
			
			return master.tank;
		}
		
		return null;
	}
	
	@Override
	public void setChanged(){
		super.setChanged();
		
		BlockState state = getWorldNonnull().getBlockState(this.worldPosition);
		getWorldNonnull().sendBlockUpdated(this.worldPosition, state, state, 3);
		getWorldNonnull().updateNeighborsAt(this.worldPosition, state.getBlock());
	}
	
	public Direction getFacing(){
		return this.facing;
	}
	
	public boolean isMaster(){
		return !this.isSlave;
	}
	
	/*// TODO RenderBoundingBox gone, find alternative
	@OnlyIn(Dist.CLIENT)
	@Override
	public AABB getRenderBoundingBox(){
		BlockPos pos = getBlockPos();
		return new AABB(pos.offset(-3, -3, -3), pos.offset(3, 3, 3));
	}
	*/
	
	@Override
	public Component[] getOverlayText(Player player, @Nonnull HitResult mop, boolean hammer){
		if(Utils.isFluidRelatedItemStack(player.getItemInHand(InteractionHand.MAIN_HAND))){
			AutoLubricatorTileEntity master = master();
			if(master != null){
				Component s = null;
				if(!master.tank.isEmpty()){
					s = ((MutableComponent) master.tank.getFluid().getDisplayName()).append(": " + master.tank.getFluidAmount() + "mB");
				}else{
					s = Component.translatable(Lib.GUI + "empty");
				}
				return new Component[]{s};
			}
		}
		return null;
	}
	
	@Override
	public boolean useNixieFont(@Nonnull Player player, @Nonnull HitResult mop){
		return false;
	}
	
	@Override
	public InteractionResult interact(@Nonnull Direction side, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull ItemStack heldItem, float hitX, float hitY, float hitZ){
		AutoLubricatorTileEntity master = master();
		if(master != null){
			if(!this.level.isClientSide && FluidUtil.interactWithFluidHandler(player, hand, master.tank)){
				setChanged();
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}
	
	int count = 0;
	int countClient = 0;
	int lastTank = 0;
	
	@Override
	public void tickClient(){
		if(this.isSlave){
			return;
		}
		
		if(!this.tank.isEmpty() && LubricantHandler.isValidLube(this.tank.getFluid()) && this.tank.getFluidAmount() >= LubricantHandler.getLubeAmount(this.tank.getFluid())){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null){
				BlockEntity master = handler.isPlacedCorrectly(this.level, this, this.facing);
				if(master != null && handler.isMachineEnabled(this.level, master)){
					handler.lubricateClient((ClientLevel) this.level, this.tank.getFluid().getFluid(), this.count, master);
					
					if(this.countClient++ % 50 == 0){
						this.countClient = this.level.random.nextInt(40);
						handler.spawnLubricantParticles((ClientLevel) this.level, this, this.facing, master);
					}
				}
			}
		}
	}
	
	@Override
	public void tickServer(){
		if(this.isSlave){
			return;
		}
		
		if(!this.tank.isEmpty() && LubricantHandler.isValidLube(this.tank.getFluid()) && this.tank.getFluidAmount() >= LubricantHandler.getLubeAmount(this.tank.getFluid())){
			BlockPos target = this.worldPosition.relative(this.facing);
			BlockEntity te = this.level.getBlockEntity(target);
			
			ILubricationHandler<BlockEntity> handler = LubricatedHandler.getHandlerForTile(te);
			if(handler != null){
				BlockEntity master = handler.isPlacedCorrectly(this.level, this, this.facing);
				if(master != null && handler.isMachineEnabled(this.level, master)){
					handler.lubricateServer((ServerLevel) this.level, this.tank.getFluid().getFluid(), this.count, master);
					
					if(this.count++ % 4 == 0){
						this.tank.drain(LubricantHandler.getLubeAmount(this.tank.getFluid()), FluidAction.EXECUTE);
					}
					
					setChanged();
				}
			}
		}
		
		if(!this.level.isClientSide && this.lastTank != this.tank.getFluidAmount()){
			this.lastTank = this.tank.getFluidAmount();
			setChanged();
		}
	}
}
