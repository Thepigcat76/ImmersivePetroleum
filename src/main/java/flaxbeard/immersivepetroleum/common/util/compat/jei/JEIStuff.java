package flaxbeard.immersivepetroleum.common.util.compat.jei;

import java.util.Map;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.client.gui.CokerUnitScreen;
import flaxbeard.immersivepetroleum.client.gui.DistillationTowerScreen;
import flaxbeard.immersivepetroleum.client.gui.HydrotreaterScreen;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

@JeiPlugin
public class JEIStuff implements IModPlugin{
	private static final ResourceLocation ID = ResourceUtils.ip("main");
	
	private RecipeType<DistillationTowerRecipe> distillation_type;
	private RecipeType<CokerUnitRecipe> coker_type;
	private RecipeType<HighPressureRefineryRecipe> recovery_type;
	
	@Override
	@Nonnull
	public ResourceLocation getPluginUid(){
		return ID;
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration){
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		
		DistillationRecipeCategory distillation = new DistillationRecipeCategory(guiHelper);
		CokerUnitRecipeCategory coker = new CokerUnitRecipeCategory(guiHelper);
		HighPressureRefineryRecipeCategory recovery = new HighPressureRefineryRecipeCategory(guiHelper);
		
		this.distillation_type = distillation.getRecipeType();
		this.coker_type = coker.getRecipeType();
		this.recovery_type = recovery.getRecipeType();
		
		registration.addRecipeCategories(distillation, coker, recovery);
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration){
		registerRecipes(registration, this.distillation_type, DistillationTowerRecipe.recipes);
		registerRecipes(registration, this.coker_type, CokerUnitRecipe.recipes);
		registerRecipes(registration, this.recovery_type, HighPressureRefineryRecipe.recipes);
	}
	
	private <T extends Recipe<?>> void registerRecipes(IRecipeRegistration reg, RecipeType<T> type, Map<ResourceLocation, RecipeHolder<T>> map){
		reg.addRecipes(type, map.values().stream().map(f -> f.value()).toList());
	}
	
	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration){
		registration.addRecipeCatalyst(IPContent.Multiblock.DISTILLATIONTOWER.iconStack(), this.distillation_type);
		registration.addRecipeCatalyst(IPContent.Multiblock.COKERUNIT.iconStack(), this.coker_type);
		registration.addRecipeCatalyst(IPContent.Multiblock.HYDROTREATER.iconStack(), this.recovery_type);
	}
	
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration){
		
		// TODO After DistillationTowerScreen re-implementation
		//registration.addRecipeClickArea(DistillationTowerScreen.class, 85, 19, 18, 51, this.distillation_type);
		
		// TODO After CokerUnitScreen re-implementation
		//Have to use four of these so that  they don't overlap
		//registration.addRecipeClickArea(CokerUnitScreen.class, 59, 21, 15, 67, this.coker_type);
		//registration.addRecipeClickArea(CokerUnitScreen.class, 64, 63, 73, 25, this.coker_type);
		//registration.addRecipeClickArea(CokerUnitScreen.class, 127, 21, 15, 67, this.coker_type);
		//registration.addRecipeClickArea(CokerUnitScreen.class, 81, 21, 39, 42, this.coker_type);
		
		// TODO After HydrotreaterScreen re-implementation
		//registration.addRecipeClickArea(HydrotreaterScreen.class, 55, 9, 32, 51, this.recovery_type);
	}
}
