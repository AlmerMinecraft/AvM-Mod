package net.almer.avm_mod.item;

import net.minecraft.item.FoodComponent;

public class ModFoodComponent {
    public static final FoodComponent RAMEN_NOODLES = new FoodComponent.Builder().hunger(10).saturationModifier(0.6f).build();
    public static final FoodComponent PANCAKE = new FoodComponent.Builder().hunger(2).saturationModifier(0.6f).build();
    public static final FoodComponent SYROPED_PANCAKES = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodComponent TOMATO = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent CHEESE = new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build();
    public static final FoodComponent PIZZA = new FoodComponent.Builder().hunger(10).saturationModifier(0.6f).build();
    public static final FoodComponent FRIED_CHICKEN = new FoodComponent.Builder().hunger(10).saturationModifier(0.6f).build();
    public static final FoodComponent GLAZED_DONUT = new FoodComponent.Builder().hunger(5).saturationModifier(0.6f).build();
    public static final FoodComponent BURGER = new FoodComponent.Builder().hunger(10).saturationModifier(0.6f).build();
}
