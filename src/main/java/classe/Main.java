package classe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();

        System.out.println("=== TESTS SIMPLES DataRetriever ===\n");

        // --- Tes tests existants ---
        try {
            Dish dish = retriever.findDishById(1);
            if (dish != null) {
                System.out.println("✓ findDishById(1) OK");
                System.out.println("  Nom: " + dish.getName());
                System.out.println("  Prix: " + dish.getPrice());
                System.out.println("  Ingrédients: " + dish.getDishIngredients().size());
                System.out.println("  Coût: " + dish.getDishCost());
                System.out.println("  Marge: " + dish.getGrossMargin());
            }
        } catch (Exception e) {
            System.out.println("✗ findDishById: " + e.getMessage());
        }

        // --- NOUVEAUX TESTS : GESTION DE STOCKS (TD4) ---
        System.out.println("\n=== TESTS GESTION DE STOCKS (TD4) ===");

        try {
            // 1. On récupère l'ingrédient 'Laitue' (ID 1)
            Ingredient laitue = new Ingredient();
            laitue.setId(1);
            laitue.setName("Laitue");
            laitue.setPrice(800.0);
            laitue.setCategory(CategoryEnum.VEGETABLE);

            // 2. On définit les mouvements selon le sujet (Page 4)
            List<StockMovement> mouvements = new ArrayList<>();

            // Stock Initial (Mouvement fictif ID 1 pour le test)
            mouvements.add(new StockMovement(1, new StockValue(5.0, UnitType.KG),
                    MovementTypeEnum.IN, Instant.parse("2024-01-01T08:00:00Z")));

            // Sortie de stock (Mouvement ID 6 selon le tableau du sujet)
            mouvements.add(new StockMovement(6, new StockValue(0.2, UnitType.KG),
                    MovementTypeEnum.OUT, Instant.parse("2024-01-06T12:00:00Z")));

            laitue.setStockMovementList(mouvements);

            // 3. Sauvegarde dans la base (avec le "ON CONFLICT DO NOTHING")
            retriever.saveIngredient(laitue);
            System.out.println("✓ Mouvements de stock sauvegardés pour la Laitue");

            // 4. Vérification du calcul du stock à T = 2024-01-06 12:00
            Instant tTest = Instant.parse("2024-01-06T12:00:00Z");
            StockValue stockActuel = laitue.getStockValueAt(tTest);

            System.out.println("  Vérification stock Laitue à " + tTest + " :");
            System.out.println("  Stock attendu: 4.8 | Stock calculé: " + stockActuel.getQuantity());

            if (stockActuel.getQuantity() == 4.8) {
                System.out.println("  => RÉSULTAT CONFORME AU SUJET ✓");
            } else {
                System.out.println("  => ÉCHEC DU CALCUL ✗");
            }

        } catch (Exception e) {
            System.out.println("✗ Erreur Test Stock: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== FIN TESTS ===");
    }
}