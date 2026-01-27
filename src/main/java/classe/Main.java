package classe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();

        System.out.println("=== TESTS SIMPLES DataRetriever ===\n");

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

        System.out.println("\n=== TESTS GESTION DE STOCKS (TD4) ===");

        try {
            Ingredient laitue = new Ingredient();
            laitue.setId(1);
            laitue.setName("Laitue");
            laitue.setPrice(800.0);
            laitue.setCategory(CategoryEnum.VEGETABLE);
            List<StockMovement> mouvements = new ArrayList<>();

            mouvements.add(new StockMovement(1, new StockValue(5.0, UnitType.KG),
                    MovementTypeEnum.IN, Instant.parse("2024-01-01T08:00:00Z")));
            mouvements.add(new StockMovement(6, new StockValue(0.2, UnitType.KG),
                    MovementTypeEnum.OUT, Instant.parse("2024-01-06T12:00:00Z")));
            laitue.setStockMovementList(mouvements);


            retriever.saveIngredient(laitue);
            System.out.println("✓ Mouvements de stock sauvegardés pour la Laitue");

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

        //annexe//
        Ingredient tomate = new Ingredient();
        tomate.setId(2);
        tomate.setName("Tomate");
        tomate.setPrice(1200.0);
        tomate.setCategory(CategoryEnum.VEGETABLE);

        List<StockMovement> moveTomate = new ArrayList<>();
        moveTomate.add(new StockMovement(10, new StockValue(10.0, UnitType.KG),
                MovementTypeEnum.IN, Instant.now()));
        tomate.setStockMovementList(moveTomate);

        retriever.saveIngredient(tomate);
        System.out.println("✓ Stock de Tomates mis à jour (10kg dispo)");
        try {
            System.out.println("--- Test Sauvegarde Commande ---");
            Dish salade = retriever.findDishById(1);
            Order nlleCommande = new Order();
            nlleCommande.setReference("ORD00001");
            nlleCommande.getDishOrders().add(new DishOrder(salade, 2));

            retriever.saveOrder(nlleCommande);
            System.out.println("✓ Commande ORD00001 enregistrée avec succès !");

            Order found = retriever.findOrderByReference("ORD00001");
            System.out.println("✓ Recherche OK : Commande ID " + found.getId());

        } catch (RuntimeException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
        System.out.println("\n=== FIN TESTS ===");
    }



}