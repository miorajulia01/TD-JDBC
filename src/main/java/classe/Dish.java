package classe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<DishIngredient> dishIngredients;


    public Dish() {
        this.dishIngredients = new ArrayList<>();
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.dishIngredients = new ArrayList<>();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients != null ? dishIngredients : new ArrayList<>();
    }


    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (dishIngredients != null) {
            for (DishIngredient di : dishIngredients) {
                if (di != null && di.getIngredient() != null) {
                    ingredients.add(di.getIngredient());
                }
            }
        }
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            this.dishIngredients = new ArrayList<>();
            return;
        }

        List<DishIngredient> newDishIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient != null) {
                DishIngredient di = new DishIngredient();
                di.setIdIngredient(ingredient.getId());
                di.setIngredient(ingredient);
                di.setQuantityRequired(1.0);
                di.setUnit(UnitType.PCS);
                newDishIngredients.add(di);
            }
        }
        this.dishIngredients = newDishIngredients;
    }


    public Double getDishCost() {
        double totalCost = 0.0;

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return totalCost;
        }

        for (DishIngredient dishIngredient : dishIngredients) {
            if (dishIngredient != null) {
                Ingredient ingredient = dishIngredient.getIngredient();
                Double ingredientPrice = ingredient != null ? ingredient.getPrice() : null;
                Double quantity = dishIngredient.getQuantityRequired();

                if (ingredientPrice != null && quantity != null) {
                    totalCost += ingredientPrice * quantity;
                }
            }
        }
        return totalCost;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException("Impossible de calculer la marge : " +
                    "le prix de vente n'est pas encore fixé.");
        }
        return price - getDishCost();
    }


    public void addIngredient(Ingredient ingredient, Double quantity, UnitType unit) {
        if (ingredient == null) return;

        DishIngredient di = new DishIngredient();
        di.setIdIngredient(ingredient.getId());
        di.setIngredient(ingredient);
        di.setQuantityRequired(quantity);
        di.setUnit(unit);

        if (dishIngredients == null) {
            dishIngredients = new ArrayList<>();
        }
        dishIngredients.add(di);
    }


    public DishIngredient findDishIngredient(Integer ingredientId) {
        if (dishIngredients == null || ingredientId == null) return null;

        for (DishIngredient di : dishIngredients) {
            if (di != null && ingredientId.equals(di.getIdIngredient())) {
                return di;
            }
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", price=" + price +
                ", ingredientsCount=" + (dishIngredients != null ? dishIngredients.size() : 0) +
                ", dishCost=" + getDishCost() +
                '}';
    }
}