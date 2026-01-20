package classe;

public class DishIngredient {
        private Integer id;
        private Integer idDish;
        private Integer idIngredient;
        private Double quantityRequired;
        private UnitType unit;
        private Ingredient ingredient;  // Pour navigation (optionnel)

        // ────────────────────────────────────────────────────────────────
        // Constructeurs
        // ────────────────────────────────────────────────────────────────
        public DishIngredient() {}

        public DishIngredient(Integer idDish, Integer idIngredient,
                              Double quantityRequired, UnitType unit) {
            this.idDish = idDish;
            this.idIngredient = idIngredient;
            this.quantityRequired = quantityRequired;
            this.unit = unit;
        }

        // Constructeur avec ingrédient complet
        public DishIngredient(Integer idDish, Ingredient ingredient,
                              Double quantityRequired, UnitType unit) {
            this.idDish = idDish;
            this.idIngredient = ingredient != null ? ingredient.getId() : null;
            this.ingredient = ingredient;
            this.quantityRequired = quantityRequired;
            this.unit = unit;
        }

        // ────────────────────────────────────────────────────────────────
        // Getters et Setters (COMPLETS - toutes les méthodes manquantes)
        // ────────────────────────────────────────────────────────────────
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getIdDish() {
            return idDish;
        }

        public void setIdDish(Integer idDish) {
            this.idDish = idDish;
        }

        public Integer getIdIngredient() {
            return idIngredient;
        }

        public void setIdIngredient(Integer idIngredient) {
            this.idIngredient = idIngredient;
        }

        public Double getQuantityRequired() {
            return quantityRequired;
        }

        public void setQuantityRequired(Double quantityRequired) {
            this.quantityRequired = quantityRequired;
        }

        public UnitType getUnit() {
            return unit;
        }

        public void setUnit(UnitType unit) {
            this.unit = unit;
        }

        public Ingredient getIngredient() {
            return ingredient;
        }

        public void setIngredient(Ingredient ingredient) {
            this.ingredient = ingredient;
            if (ingredient != null && this.idIngredient == null) {
                this.idIngredient = ingredient.getId();
            }
        }

        // ────────────────────────────────────────────────────────────────
        // Méthode utilitaire pour obtenir le nom de l'ingrédient
        // ────────────────────────────────────────────────────────────────
        public String getIngredientName() {
            return ingredient != null ? ingredient.getName() : null;
        }

        // ────────────────────────────────────────────────────────────────
        // Méthode utilitaire pour obtenir le prix unitaire
        // ────────────────────────────────────────────────────────────────
        public Double getIngredientPrice() {
            return ingredient != null ? ingredient.getPrice() : null;
        }

        @Override
        public String toString() {
            return "DishIngredient{" +
                    "id=" + id +
                    ", idDish=" + idDish +
                    ", idIngredient=" + idIngredient +
                    ", quantityRequired=" + quantityRequired +
                    ", unit=" + unit +
                    ", ingredientName=" + getIngredientName() +
                    '}';
        }
}
