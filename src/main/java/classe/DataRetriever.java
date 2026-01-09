package classe;

import config.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection;
    private String name;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    public Dish findDishById(Integer id) {
        String sqlDish = "SELECT id, name, dish_type, price FROM dish WHERE id = ?";
        String sqlIngredients = "SELECT id, name, price, category FROM ingredient WHERE id_dish = ?";
        Dish dish = null;

        try (Connection conn = dbConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlDish)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException("Aucun plat trouvé avec l'ID: " + id);
                    }
                    dish = new Dish();
                    dish.setId(rs.getInt("id"));
                    dish.setName(rs.getString("name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    dish.setPrice(rs.getDouble("price"));
                }
            }


            if (dish != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlIngredients)) {
                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        List<Ingredient> ingredients = new ArrayList<>();
                        while (rs.next()) {
                            Ingredient ingredient = new Ingredient();
                            ingredient.setId(rs.getInt("id"));
                            ingredient.setName(rs.getString("name"));
                            ingredient.setPrice(rs.getDouble("price"));
                            ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                            ingredient.setDish(dish);

                            ingredients.add(ingredient);
                        }
                        dish.setIngredients(ingredients);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat", e);
        }
        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        String sqlIngredient = """
            SELECT i.id, i.name, i.price, i.category, i.id_dish, d.name as dish_name 
            FROM Ingredient i 
            LEFT JOIN dish d ON i.id_dish = d.id 
            ORDER BY i.id 
            LIMIT ? OFFSET ?
            """;

        List<Ingredient> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlIngredient)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                    if (rs.getObject("id_dish") != null) {
                        Dish dish = new Dish();
                        dish.setId(rs.getInt("id_dish"));
                        dish.setName(rs.getString("dish_name"));
                        ingredient.setDish(dish);
                    }

                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }
        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        String checkSql = "SELECT COUNT(*) FROM ingredient WHERE name = ?";
        String insertSql = "INSERT INTO ingredient(name, price, category, id_dish) VALUES (?, ?, ?, ?)";

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

                try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    for (Ingredient ing : newIngredients) {
                        insertPs.setString(1, ing.getName());
                        insertPs.setDouble(2, ing.getPrice());
                        insertPs.setString(3, ing.getCategory().name());

                        // Gérer l'association avec un plat (peut être null)
                        if (ing.getDish() != null) {
                            insertPs.setInt(4, ing.getDish().getId());
                        } else {
                            insertPs.setNull(4, Types.INTEGER);
                        }

                        insertPs.executeUpdate();

                        ResultSet generatedKeys = insertPs.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            ing.setId(generatedKeys.getInt(1));
                        }
                    }
                }

                conn.commit();
                return newIngredients;

            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la création des ingrédients", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }


    public Dish saveDish(Dish dishToSave) {
        String checkSql = "SELECT COUNT(*) FROM dish WHERE id = ?";
        String insertDish = "INSERT INTO dish(name, dish_type, price) VALUES (?, ?, ?)";
        String updateDish = "UPDATE dish SET name = ?, dish_type = ? ,price = ? WHERE id = ?";
        String updateIngredient = "UPDATE ingredient SET id_dish = ? WHERE id = ?";
        String removeIngredients = "UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?";

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                boolean dishExists = false;
                int dishId = dishToSave.getId();

                if (dishId > 0) {
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, dishId);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            dishExists = true;
                        }
                    }
                }

                if (!dishExists) {
                    try (PreparedStatement ps = conn.prepareStatement(insertDish, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, dishToSave.getName());
                        ps.setString(2, dishToSave.getDishType().name());
                        ps.setDouble(3, dishToSave.getPrice());
                        ps.executeUpdate();

                        ResultSet rs = ps.getGeneratedKeys();
                        if (rs.next()) {
                            dishId = rs.getInt(1);
                            dishToSave.setId(dishId);
                        }
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(updateDish)) {
                        ps.setString(1, dishToSave.getName());
                        ps.setString(2, dishToSave.getDishType().name());
                        ps.setDouble(3, dishToSave.getPrice());
                        ps.setInt(4, dishId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(removeIngredients)) {
                    ps.setInt(1, dishId);
                    ps.executeUpdate();
                }

                if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(updateIngredient)) {
                        for (Ingredient ing : dishToSave.getIngredients()) {
                            ps.setInt(1, dishId);
                            ps.setInt(2, ing.getId());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
                return findDishById(dishId);

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la sauvegarde du plat", e);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String query = """
            SELECT DISTINCT d.id, d.name, d.dish_type 
            FROM Dish d 
            JOIN Ingredient i ON d.id = i.id_dish 
            WHERE LOWER(i.name) LIKE LOWER(?) 
            ORDER BY d.id
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, "%" + ingredientName + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dishes.add(dish);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de plats par ingrédient", e);
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
            SELECT i.id, i.name, i.price, i.category, d.name as dish_name
            FROM ingredient i
            LEFT JOIN dish d ON i.id_dish = d.id
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

            try (ResultSet rs = ps.executeQuery()) {
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
                        ing.setDish(dish);
                    }

                    result.add(ing);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par critères", e);
        }
        return result;
    }
}