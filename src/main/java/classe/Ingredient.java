package classe;

import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;  // Prix unitaire de l'ingrédient

    // ────────────────────────────────────────────────────────────────
    // NOTE : quantity n'est plus ici !
    // La quantité spécifique à un plat se trouve dans DishIngredient.quantityRequired
    // ────────────────────────────────────────────────────────────────

    // ────────────────────────────────────────────────────────────────
    // NOTE : dish n'est plus forcément nécessaire car relation ManyToMany
    // Un ingrédient peut appartenir à plusieurs plats
    // On le garde optionnel pour compatibilité
    // ────────────────────────────────────────────────────────────────
    private Dish dish;  // Optionnel - pour navigation simple

    // ────────────────────────────────────────────────────────────────
    // Constructeurs
    // ────────────────────────────────────────────────────────────────
    public Ingredient() {
    }

    public Ingredient(Integer id) {
        this.id = id;
    }

    public Ingredient(Integer id, String name, CategoryEnum category, Double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // Constructeur sans catégorie (utile pour certains cas)
    public Ingredient(Integer id, String name, Double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // ────────────────────────────────────────────────────────────────
    // Getters et Setters
    // ────────────────────────────────────────────────────────────────
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

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    // ────────────────────────────────────────────────────────────────
    // Méthode getDishName - pour affichage seulement
    // ────────────────────────────────────────────────────────────────
    public String getDishName() {
        return dish == null ? null : dish.getName();
    }

    // ────────────────────────────────────────────────────────────────
    // ATTENTION : getQuantity() et setQuantity() sont dépréciés !
    // La quantité appartient à DishIngredient, pas à Ingredient
    // On les garde pour compatibilité avec ancien code, mais elles renvoient null
    // ────────────────────────────────────────────────────────────────
    public Double getQuantity() {
        // Renvoie null car la quantité est dans DishIngredient
        // Méthode gardée pour compatibilité avec ancien code
        return null;
    }

    public void setQuantity(Double quantity) {
        // Ne fait rien - la quantité est gérée dans DishIngredient
        // On pourrait logger un warning en production
        System.err.println("Attention: setQuantity() est obsolète. Utilisez DishIngredient.setQuantityRequired()");
    }

    // ────────────────────────────────────────────────────────────────
    // equals, hashCode, toString
    // ────────────────────────────────────────────────────────────────
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                category == that.category &&
                Objects.equals(price, that.price);
        // On ne compare plus dish car ManyToMany
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, price);
        // On n'inclut plus dish dans le hash
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + price +
                (dish != null ? ", dishName=" + dish.getName() : "") +
                '}';
        // On n'affiche plus quantity car elle n'est plus ici
    }
}