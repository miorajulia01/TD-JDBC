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
    private List<StockMovement> stockMovementList = new ArrayList<>();
    public Ingredient() {}

    public StockValue getStockValueAt(Instant t) {
        double total = 0.0;
        for (StockMovement m : stockMovementList) {
            if (m.getCreationDatetime() != null && !m.getCreationDatetime().isAfter(t)) {
                if (m.getType() == MovementTypeEnum.IN) {
                    total += m.getValue().getQuantity();
                } else if (m.getType() == MovementTypeEnum.OUT) {
                    total -= m.getValue().getQuantity();
                }
            }
        }
        return new StockValue(total, UnitType.KG);
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

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