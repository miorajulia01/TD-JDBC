package classe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //bonus
        Map<String, Double> stocks = new HashMap<>();
        stocks.put("Laitue", 5.0);
        stocks.put("Tomate", 4.0);
        stocks.put("Poulet", 10.0);
        stocks.put("Chocolat", 3.0);
        stocks.put("Beurre", 2.5);

        System.out.println("=== VERIFICATION DES SORTIES  ===");
        double sortieTomatePCS = 5.0;

        double sortieTomateKG = Convertion.convertToKG("Tomate", sortieTomatePCS, UnitType.PCS);
        double stockFinalTomate = stocks.get("Tomate") - sortieTomateKG;
        System.out.println("Tomate : Sortie 0.5 KG | Stock Final : " + stockFinalTomate + " KG (Attendu: 3.5)");

        double sortieChocoL = 1.0;
        double sortieChocoKG = Convertion.convertToKG("Chocolat", sortieChocoL, UnitType.L);
        double stockFinalChoco = stocks.get("Chocolat") - sortieChocoKG;
        System.out.println("Chocolat : Sortie 0.4 KG | Stock Final : " + stockFinalChoco + " KG (Attendu: 2.6)");

        double sortieBeurreL = 1.0;
        double sortieBeurreKG = Convertion.convertToKG("Beurre", sortieBeurreL, UnitType.L);
        double stockFinalBeurre = stocks.get("Beurre") - sortieBeurreKG;
        System.out.println("Beurre : Sortie 0.2 KG | Stock Final : " + stockFinalBeurre + " KG (Attendu: 2.3)");
        System.out.println("\n=== FIN TESTS ===");

      //évaluation
        Table table1 = new Table(1, 1);
        TableOrder occupation = new TableOrder(table1, Instant.now(), Instant.now().plusSeconds(7200));
        Order maCommande = new Order();
        maCommande.setReference("TEST-EXAM");
        maCommande.setCreationDatetime(Instant.now());
        maCommande.setTableOrder(occupation); // Liaison cruciale

        try {
            System.out.println("Tentative de sauvegarde de la commande...");
            retriever.saveOrder(maCommande);
            System.out.println("Succès !");
        } catch (RuntimeException e) {
            System.err.println("ERREUR ATTENDUE : " + e.getMessage());
        }
    }



}