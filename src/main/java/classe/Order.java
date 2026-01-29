package classe;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders = new ArrayList<>();

    public Order() {}

    public Order(Integer id, String reference, Instant creationDatetime) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        if (dishOrders != null) {
            this.dishOrders = dishOrders;
        }
    }

    private TableOrder table;

    public TableOrder getTableOrder() {
        return table;
    }

    public void setTableOrder(TableOrder table) {
        this.table = table;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }


    public Double getTotalAmountWithoutVAT() {
        return dishOrders.stream()
                .mapToDouble(item -> item.getDish().getPrice() * item.getQuantity())
                .sum();
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.20;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDatetime, dishOrders);
    }
}

