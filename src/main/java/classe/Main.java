package classe;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== TESTS SIMPLES DataRetriever ===\n");

        DataRetriever retriever = new DataRetriever();

        // Test 1: findDishById
        try {
            Dish dish = retriever.findDishById(1);
            System.out.println("✓ findDishById(1) OK");
            System.out.println("  Nom: " + dish.getName());
            System.out.println("  Prix: " + dish.getPrice());
            System.out.println("  Ingrédients: " + dish.getDishIngredients().size());

            // Test getDishCost et getGrossMargin
            System.out.println("  Coût: " + dish.getDishCost());
            System.out.println("  Marge: " + dish.getGrossMargin());
        } catch (Exception e) {
            System.out.println("✗ findDishById: " + e.getMessage());
        }

        // Test 2: findIngredients
        try {
            List<Ingredient> ingredients = retriever.findIngredients(1, 5);
            System.out.println("\n✓ findIngredients OK");
            System.out.println("  Nombre: " + ingredients.size());
        } catch (Exception e) {
            System.out.println("\n✗ findIngredients: " + e.getMessage());
        }

        // Test 3: findDishsByIngredientName
        try {
            List<Dish> plats = retriever.findDishsByIngredientName("poulet");
            System.out.println("\n✓ findDishsByIngredientName OK");
            System.out.println("  Plats trouvés: " + plats.size());
        } catch (Exception e) {
            System.out.println("\n✗ findDishsByIngredientName: " + e.getMessage());
        }

        System.out.println("\n=== FIN TESTS ===");
    }
}
