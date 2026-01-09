package classe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = new ArrayList<>();
    }
    public Dish(){};

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id == dish.id && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }


    public Double getDishCost() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Impossible de calculer la marge : le prix de vente est null.");
        }
        return price.doubleValue() - getDishCost();
    }

}

