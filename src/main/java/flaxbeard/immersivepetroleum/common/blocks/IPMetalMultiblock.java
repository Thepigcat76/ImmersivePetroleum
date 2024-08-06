package flaxbeard.immersivepetroleum.common.blocks;

@Deprecated(forRemoval = true)
public class IPMetalMultiblock/*<T extends MultiblockPartBlockEntity<T> & IPCommonTickableTile> extends MetalMultiblockBlock<T>*/{
	/*
	private final MultiblockBEType<T> multiblockBEType;
	
	public IPMetalMultiblock(MultiblockBEType<T> te){
		super(te, BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL)
				.strength(3, 15)
				.requiresCorrectToolForDrops()
				.isViewBlocking((state, blockReader, pos) -> false)
				.noOcclusion()
				.dynamicShape()
		);
		this.multiblockBEType = te;
	}
	
	@Override
	public <E extends BlockEntity> BlockEntityTicker<E> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<E> type){
		return IPBlockBase.createCommonTicker(world.isClientSide, type, multiblockBEType.master());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(hand == InteractionHand.MAIN_HAND){
			BlockEntity te = world.getBlockEntity(pos);
			
			if(te instanceof MenuProvider menuProvider){
				if(te instanceof ICanSkipGUI skippable && !player.getItemInHand(hand).isEmpty() && skippable.skipGui(hit.getDirection())){
					return InteractionResult.FAIL;
				}
				
				if(!player.isShiftKeyDown()){
					if(player instanceof ServerPlayer serverPlayer){
						if(menuProvider instanceof IHasGUIInteraction<?> interaction){
							interaction = interaction.getGuiMaster();
							if(interaction != null && interaction.canUseGui(player)){
								
								// Between these lines is basicly a direct copy-paste from IEEntityBlock
								// ----------------------------------------------------------------------------
								
								// This can be removed once IEBaseContainerOld is gone
								AbstractContainerMenu tempMenu = interaction.createMenu(0, player.getInventory(), player);
								if(tempMenu instanceof IEBaseContainerOld<?>)
									NetworkHooks.openScreen(serverPlayer, interaction, ((BlockEntity) interaction).getBlockPos());
								else
									NetworkHooks.openScreen(serverPlayer, interaction);
								// ----------------------------------------------------------------------------
							}
						}
					}
					return InteractionResult.SUCCESS;
				}
			}
		}
		return super.use(state, world, pos, player, hand, hit);
	}
	*/
}
