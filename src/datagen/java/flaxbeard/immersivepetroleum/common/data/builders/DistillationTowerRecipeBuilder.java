package flaxbeard.immersivepetroleum.common.data.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.common.util.ChancedItemStack;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Distillation Recipe creation using DataGeneration
 *
 * @author TwistedGate
 */
public class DistillationTowerRecipeBuilder extends IPRecipeBuilder<DistillationTowerRecipeBuilder>{
	
	public static DistillationTowerRecipeBuilder builder(FluidStack... output){
		DistillationTowerRecipeBuilder b = new DistillationTowerRecipeBuilder();
		b.fluidOutputs.addAll(Arrays.asList(output));
		return b;
	}
	
	private FluidTagInput fluidInput;
	private final List<ChancedItemStack> byproducts = new ArrayList<>();
	private final List<FluidStack> fluidOutputs = new ArrayList<>();
	int energy;
	int time;
	
	private DistillationTowerRecipeBuilder(){
	}
	
	public DistillationTowerRecipeBuilder addByproduct(ChancedItemStack output){
		this.byproducts.add(output);
		return this;
	}
	
	public DistillationTowerRecipeBuilder fluidInput(FluidTagInput input){
		this.fluidInput = input;
		return this;
	}
	
	public DistillationTowerRecipeBuilder setTimeAndEnergy(int time, int energy){
		this.time = time;
		this.energy = energy;
		return this;
	}
	
	public void build(RecipeOutput out, ResourceLocation name){
		DistillationTowerRecipe recipe = new DistillationTowerRecipe(this.fluidOutputs, this.byproducts, this.fluidInput, this.energy, this.time);
		out.accept(name, recipe, null, getConditions());
	}
}
