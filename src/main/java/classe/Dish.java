package classe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double Price;
    private List<DishIngredient> dishIngredients;

    // ────────────────────────────────────────────────────────────────
    // Constructeurs
    // ────────────────────────────────────────────────────────────────
    public Dish() {
        this.dishIngredients = new ArrayList<>();
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.Price = sellingPrice;
        this.dishIngredients = new ArrayList<>();
    }

    // ────────────────────────────────────────────────────────────────
    // Getters et Setters
    // ────────────────────────────────────────────────────────────────
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public Double getSellingPrice() { return Price; }
    public void setSellingPrice(Double sellingPrice) { this.Price = sellingPrice; }

    public List<DishIngredient> getDishIngredients() { return dishIngredients; }
    public void setDishIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients != null ? dishIngredients : new ArrayList<>();
    }

    // ────────────────────────────────────────────────────────────────
    // Méthode de compatibilité : getPrice() retourne sellingPrice
    // ────────────────────────────────────────────────────────────────
    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        this.Price = price;
    }

    // ────────────────────────────────────────────────────────────────
    // Méthode de compatibilité : getIngredients()
    // Extraie les ingrédients purs de dishIngredients
    // ────────────────────────────────────────────────────────────────
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (dishIngredients != null) {
            for (DishIngredient di : dishIngredients) {
                if (di.getIngredient() != null) {
                    // On met à jour la référence bidirectionnelle si besoin
                    di.getIngredient().setDish(this);
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

        // Conversion de List<Ingredient> en List<DishIngredient>
        List<DishIngredient> newDishIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            DishIngredient di = new DishIngredient();
            di.setIdIngredient(ingredient.getId());
            di.setIngredient(ingredient);
            // On utilise 1.0 comme quantité par défaut (à ajuster selon besoins)
            di.setQuantityRequired(1.0);
            di.setUnit(UnitType.PCS);
            newDishIngredients.add(di);
        }
        this.dishIngredients = newDishIngredients;
    }

    // ────────────────────────────────────────────────────────────────
    // getDishCost() - Calcule le coût avec quantités
    // ────────────────────────────────────────────────────────────────
    public Double getDishCost() {
        double totalCost = 0.0;

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return totalCost;
        }

        for (DishIngredient dishIngredient : dishIngredients) {
            Ingredient ingredient = dishIngredient.getIngredient();
            if (ingredient != null &&
                    ingredient.getPrice() != null &&
                    dishIngredient.getQuantityRequired() != null) {
                totalCost += ingredient.getPrice() * dishIngredient.getQuantityRequired();
            }
        }
        return totalCost;
    }


    public Double getGrossMargin() {
        if (Price == null) {
            throw new IllegalStateException("Impossible de calculer la marge : " +
                    "le prix de vente n'est pas encore fixé.");
        }
        return Price - getDishCost();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) &&
                Objects.equals(name, dish.name) &&
                dishType == dish.dishType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", sellingPrice=" + Price +
                ", dishIngredientsCount=" + (dishIngredients != null ? dishIngredients.size() : 0) +
                '}';
    }
}