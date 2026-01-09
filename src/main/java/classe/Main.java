package classe;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DataRetriever retriever = new DataRetriever();

        System.out.println("TEST a) findDishById(1)");
        System.out.println("Données initiales:");
        System.out.println("- Dish ID 1: Salade fraîche (START)");
        System.out.println("- Ingredient ID 1: Laitue (id_dish=1)");
        System.out.println("- Ingredient ID 2: Tomate (id_dish=1)");

        Dish dish = retriever.findDishById(1);

        System.out.println("\nRésultat OBTENU:");
        System.out.println("- Nom: " + dish.getName());
        System.out.println("- Type: " + dish.getDishType());
        System.out.println("- Nombre ingrédients: " + dish.getIngredients().size());

        if (dish.getIngredients().size() == 2) {
            System.out.println("- Ingrédient 1: " + dish.getIngredients().get(0).getName());
            System.out.println("- Ingrédient 2: " + dish.getIngredients().get(1).getName());
        }

        System.out.println("\nRésultat ATTENDU:");
        System.out.println("- Salade fraîche avec 2 ingrédients (Laitue et Tomate)");
        System.out.println("\n✓ TEST RÉUSSI si les deux ingrédients sont Laitue et Tomate");
        System.out.println("\n" + "=".repeat(60) + "\n");


        System.out.println("TEST c) findIngredients(page=2, size=2)");
        System.out.println("Données initiales (par ordre d'ID):");
        System.out.println("1. Laitue (id_dish=1)");
        System.out.println("2. Tomate (id_dish=1)");
        System.out.println("3. Poulet (id_dish=2)");
        System.out.println("4. Chocolat (id_dish=4)");
        System.out.println("5. Beurre (id_dish=4)");

        System.out.println("\nCalcul pagination:");
        System.out.println("- Page 1 (offset 0): Laitue, Tomate");
        System.out.println("- Page 2 (offset 2): Poulet, Chocolat");
        System.out.println("- Page 3 (offset 4): Beurre");

        List<Ingredient> ingredients = retriever.findIngredients(2, 2);

        System.out.println("\nRésultat OBTENU (" + ingredients.size() + " ingrédients):");
        for (int i = 0; i < ingredients.size(); i++) {
            System.out.println((i+1) + ". " + ingredients.get(i).getName());
        }

        System.out.println("\nRésultat ATTENDU:");
        System.out.println("- Ingrédients Poulet, Chocolat");

        if (ingredients.size() == 2 &&
                ingredients.get(0).getName().equals("Poulet") &&
                ingredients.get(1).getName().equals("Chocolat")) {
            System.out.println("✓ TEST RÉUSSI");
        } else {
            System.out.println("✗ TEST ÉCHOUÉ - Vérifiez votre requête SQL de pagination");
        }

        System.out.println("\n" + "=".repeat(60) + "\n");


        System.out.println("TEST e) findDishsByIngredientName(\"eur\")");
        System.out.println("Logique de recherche:");
        System.out.println("1. Cherche les plats dont un ingrédient contient 'eur'");
        System.out.println("2. Seul 'Beurre' contient 'eur'");
        System.out.println("3. Beurre est dans le plat ID 4: Gâteau au chocolat");

        List<Dish> dishes = retriever.findDishsByIngredientName("eur");

        System.out.println("\nRésultat OBTENU (" + dishes.size() + " plat(s)):");
        for (Dish d : dishes) {
            System.out.println("- " + d.getName());
        }

        System.out.println("\nRésultat ATTENDU:");
        System.out.println("- Plat - Gâteau au chocolat");

        if (dishes.size() == 1 && dishes.get(0).getName().contains("chocolat")) {
            System.out.println("✓ TEST RÉUSSI");
        } else {
            System.out.println("✗ TEST ÉCHOUÉ - Vérifiez votre requête avec LIKE");
        }

        System.out.println("\n" + "=".repeat(60) + "\n");


        System.out.println("TEST h) findIngredientsByCriteria(\"cho\", null, \"gâteau\", 1, 10)");
        System.out.println("Logique de recherche:");
        System.out.println("1. Ingredient name LIKE '%cho%' → 'Chocolat' correspond");
        System.out.println("2. Dish name LIKE '%gâteau%' → 'Gâteau au chocolat' correspond");
        System.out.println("3. Chocolat est dans Gâteau au chocolat → DOIT être retourné");

        List<Ingredient> result = retriever.findIngredientsByCriteria("cho", null, "gâteau", 1, 10);

        System.out.println("\nRésultat OBTENU (" + result.size() + " ingrédient(s)):");
        for (Ingredient ing : result) {
            System.out.println("- " + ing.getName());
        }

        System.out.println("\nRésultat ATTENDU:");
        System.out.println("- Chocolat");

        if (result.size() == 1 && result.get(0).getName().equals("Chocolat")) {
            System.out.println("✓ TEST RÉUSSI");
        } else {
            System.out.println("✗ TEST ÉCHOUÉ - Vérifiez votre JOIN et vos conditions WHERE");
        }

        System.out.println("=== Test findDishById ===");
        Dish dish1 = retriever.findDishById(1);
        System.out.println("Plat trouvé : " + dish1);
        try {
            System.out.println("Marge brute : " + dish1.getGrossMargin());
        } catch (IllegalStateException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        Dish dish2 = retriever.findDishById(2);
        System.out.println("\nPlat trouvé : " + dish2);
        try {
            System.out.println("Marge brute : " + dish2.getGrossMargin());
        } catch (IllegalStateException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        System.out.println("\n=== Test saveDish (création) ===");
        Dish newDish = new Dish(01, "Salad russe", "Start", [pomme de terre, betterave]);
        Dish savedDish = retriever.saveDish(newDish);
        System.out.println("Plat créé : " + savedDish);
        try {
            System.out.println("Marge brute : " + savedDish.getGrossMargin());
        } catch (IllegalStateException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        System.out.println("\n=== Test saveDish (mise à jour prix) ===");
        Dish dishToUpdate = retriever.findDishById(2);
        dishToUpdate.setPrice(10.0);
        retriever.saveDish(dishToUpdate);
        System.out.println("Plat après mise à jour prix : " + dishToUpdate);
        try {
            System.out.println("Marge brute : " + dishToUpdate.getGrossMargin());
        } catch (IllegalStateException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        System.out.println("\n=== Vérification findDishById après mise à jour ===");
        Dish updated = retriever.findDishById(2);
        System.out.println("Plat relu : " + updated);
        try {
            System.out.println("Marge brute : " + updated.getGrossMargin());
        } catch (IllegalStateException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
