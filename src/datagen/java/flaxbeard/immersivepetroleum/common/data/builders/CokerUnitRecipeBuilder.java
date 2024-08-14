package flaxbeard.immersivepetroleum.common.data.builders;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class CokerUnitRecipeBuilder extends IPRecipeBuilder<CokerUnitRecipeBuilder>{
	
	public static CokerUnitRecipeBuilder builder(ItemStack itemOutput, FluidStack fluidOutput){
		CokerUnitRecipeBuilder b = new CokerUnitRecipeBuilder();
		b.itemOutput = itemOutput;
		b.fluidOutput = fluidOutput;
		return b;
	}
	
	private IngredientWithSize itemInput;
	private FluidTagInput fluidInput;
	private ItemStack itemOutput;
	private FluidStack fluidOutput;
	int energy;
	int time;
	
	private CokerUnitRecipeBuilder(){
	}
	
	public CokerUnitRecipeBuilder itemInput(IngredientWithSize input){
		this.itemInput = input;
		return this;
	}
	
	public CokerUnitRecipeBuilder fluidInput(FluidTagInput input){
		this.fluidInput = input;
		return this;
	}
	
	public CokerUnitRecipeBuilder setTimeAndEnergy(int time, int energy){
		this.time = time;
		this.energy = energy;
		return this;
	}
	
	public void build(RecipeOutput out, ResourceLocation name){
		CokerUnitRecipe recipe = new CokerUnitRecipe(this.itemOutput, this.fluidOutput, this.itemInput, this.fluidInput, this.energy, this.time);
		out.accept(name, recipe, null, getConditions());
	}
}
