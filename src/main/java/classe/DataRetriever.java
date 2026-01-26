package classe;

import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection;
    private ResultSet rs;
    private ResultSet rs1;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }


    public Dish findDishById(Integer id) {
        try (Connection conn = dbConnection.getConnection()) {
            String dishSql = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";
            Dish dish = null;

            try (PreparedStatement ps = conn.prepareStatement(dishSql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    double price = rs.getDouble("price");
                    dish.setPrice(rs.wasNull() ? null : price);
                } else {
                    return null;
                }
            }

            String ingredientsSql = """
            SELECT i.id, i.name, i.price, i.category, 
                   di.quantity_required, di.unit
            FROM ingredient i
            JOIN dish_ingredient di ON i.id = di.id_ingredient
            WHERE di.id_dish = ?
            """;

            List<DishIngredient> dishIngredients = new ArrayList<>();

            try (PreparedStatement ps = conn.prepareStatement(ingredientsSql)) {
                ps.setInt(1, id);
                ResultSet rs1 = ps.executeQuery();

                while (rs1.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs1.getInt("id"));
                    ingredient.setName(rs1.getString("name"));
                    ingredient.setPrice(rs1.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs1.getString("category")));

                    DishIngredient di = new DishIngredient();
                    di.setDish(dish);
                    di.setIngredient(ingredient);
                    di.setQuantityRequired(rs1.getDouble("quantity_required"));
                    di.setUnit(UnitType.valueOf(rs1.getString("unit")));

                    dishIngredients.add(di);
                }
            }

            dish.setDishIngredients(dishIngredients);
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur JDBC dans findDishById : " + e.getMessage());
        }
    }


    public List<Ingredient> findIngredients(int page, int size) {
        String sql = "SELECT id, name, price, category FROM ingredient ORDER BY id LIMIT ? OFFSET ?";
        List<Ingredient> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ing);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findIngredients: " + e.getMessage(), e);
        }
        return ingredients;
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
        String insertSql = "INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                for (Ingredient ing : newIngredients) {
                    try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                        checkPs.setString(1, ing.getName());
                        ResultSet rs = checkPs.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            conn.rollback();
                            throw new RuntimeException("L'ingrédient '" + ing.getName() + "' existe déjà");
                        }
                    }
                }


                List<Ingredient> saved = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (Ingredient ing : newIngredients) {
                        ps.setString(1, ing.getName());
                        ps.setDouble(2, ing.getPrice());
                        ps.setString(3, ing.getCategory().name());
                        ps.executeUpdate();


                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            ing.setId(rs.getInt(1));
                            saved.add(ing);
                        }
                    }
                }

                conn.commit();
                return saved;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur createIngredients: " + e.getMessage(), e);
        }
    }


    public Dish saveDish(Dish dishToSave) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;

            if (dishToSave.getId() == null) {
                String insertSql = "INSERT INTO dish(name, dish_type, price) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());
                    ps.setObject(3, dishToSave.getPrice());
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        dishId = rs.getInt(1);
                        dishToSave.setId(dishId);
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID du plat.");
                    }
                }
            } else {
                dishId = dishToSave.getId();
                String updateSql = "UPDATE dish SET name = ?, dish_type = ?, price = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());
                    ps.setObject(3, dishToSave.getPrice());
                    ps.setInt(4, dishId);
                    ps.executeUpdate();
                }
            }

            String deleteSql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }

            if (dishToSave.getDishIngredients() != null && !dishToSave.getDishIngredients().isEmpty()) {
                String insertDiSql = "INSERT INTO dish_ingredient(id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertDiSql)) {
                    for (DishIngredient di : dishToSave.getDishIngredients()) {
                        ps.setInt(1, dishId);


                        ps.setInt(2, di.getIngredient().getId());
                        ps.setDouble(3, di.getQuantityRequired());
                        ps.setString(4, di.getUnit().name());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return dishToSave;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur dans saveDish : " + e.getMessage());
        }
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) {
        String sql = """
            SELECT DISTINCT d.id, d.name, d.dish_type, d.price
            FROM dish d
            JOIN dish_ingredient di ON d.id = di.id_dish
            JOIN ingredient i ON i.id = di.id_ingredient
            WHERE LOWER(i.name) LIKE LOWER(?)
            ORDER BY d.id
            """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + ingredientName + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));
                dishes.add(dish);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findDishsByIngredientName: " + e.getMessage(), e);
        }
        return dishes;
    }


    public List<Ingredient> findIngredientsByCriteria(
            String ingredientName,
            CategoryEnum category,
            String dishName,
            int page,
            int size) {

        StringBuilder sql = new StringBuilder("""
            SELECT DISTINCT i.id, i.name, i.price, i.category, d.name as dish_name
            FROM ingredient i
            LEFT JOIN dish_ingredient di ON i.id = di.id_ingredient
            LEFT JOIN dish d ON d.id = di.id_dish
            WHERE 1=1
            """);

        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append(" AND LOWER(i.name) LIKE LOWER(?)");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?");
            params.add(category.name());
        }

        if (dishName != null && !dishName.trim().isEmpty()) {
            sql.append(" AND LOWER(d.name) LIKE LOWER(?)");
            params.add("%" + dishName + "%");
        }

        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);
        List<Ingredient> result = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setId(rs.getInt("id"));
                ing.setName(rs.getString("name"));
                ing.setPrice(rs.getDouble("price"));
                ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                String dishNameResult = rs.getString("dish_name");
                if (dishNameResult != null) {
                    Dish dish = new Dish();
                    dish.setName(dishNameResult);
                }
                result.add(ing);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findIngredientsByCriteria: " + e.getMessage(), e);
        }
        return result;
    }

    /// TD4 ///

    public Ingredient saveIngredient(Ingredient toSave) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            Integer ingredientId;

            // Étape 1 : Sauvegarde de l'ingrédient
            if (toSave.getId() == null) {
                String sql = "INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?::ingredient_category) RETURNING id";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, toSave.getName());
                    ps.setDouble(2, toSave.getPrice());
                    ps.setString(3, toSave.getCategory().name());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) toSave.setId(rs.getInt(1));
                }
            } else {
                String sql = "UPDATE ingredient SET name=?, price=?, category=?::ingredient_category WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, toSave.getName());
                    ps.setDouble(2, toSave.getPrice());
                    ps.setString(3, toSave.getCategory().name());
                    ps.setInt(4, toSave.getId());
                    ps.executeUpdate();
                }
            }
            ingredientId = toSave.getId();

            // Étape 2 : Sauvegarde des mouvements de stock [cite: 50]
            if (toSave.getStockMovementList() != null && !toSave.getStockMovementList().isEmpty()) {
                String moveSql = """
            INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
             VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?)
            ON CONFLICT (id) DO NOTHING
"""; // Contrainte du sujet respectée
                try (PreparedStatement ps = conn.prepareStatement(moveSql)) {
                    for (StockMovement sm : toSave.getStockMovementList()) {
                        ps.setInt(1, sm.getId()); // ID obligatoire pour détecter le conflit
                        ps.setInt(2, ingredientId);
                        ps.setDouble(3, sm.getValue().getQuantity());
                        ps.setString(4, sm.getType().name());
                        ps.setString(5, sm.getValue().getUnit().name());
                        ps.setTimestamp(6, Timestamp.from(sm.getCreationDatetime()));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            conn.commit();
            return toSave;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur saveIngredient: " + e.getMessage());
        }
    }

    //annexes

}