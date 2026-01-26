package classe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;
    private List<StockMovement> stockMovementList = new ArrayList<>(); // Initialisation [cite: 30]

    public Ingredient() {}

    // Méthode de calcul du stock à un instant T [cite: 31, 57]
    public StockValue getStockValueAt(Instant t) {
        double total = 0.0;
        for (StockMovement m : stockMovementList) {
            // On vérifie que le mouvement a eu lieu avant ou à l'instant t [cite: 57]
            if (m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t)) {
                if (m.getType() == MovementTypeEnum.IN) {
                    total += m.getValue().getQuantity(); // Entrée de stock
                } else if (m.getType() == MovementTypeEnum.OUT) {
                    total -= m.getValue().getQuantity(); // Sortie de stock
                }
            }
        }
        return new StockValue(total, UnitType.KG); // Toutes les unités sont en KG pour le moment [cite: 45]
    }

    // Getters et Setters corrigés
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; } // Corrigé : paramètre bien assigné

    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> list) { this.stockMovementList = list; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}