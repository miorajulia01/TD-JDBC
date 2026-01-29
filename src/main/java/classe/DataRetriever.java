package classe;

import config.DBConnection;
import java.sql.*;
import java.time.Instant;
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
                    int ingId = rs1.getInt("id");
                    ingredient.setId(ingId);
                    ingredient.setName(rs1.getString("name"));
                    ingredient.setPrice(rs1.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs1.getString("category")));

                    ingredient.setStockMovementList(findStockMovementsByIngredientId(conn, ingId));
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

    private List<StockMovement> findStockMovementsByIngredientId(Connection conn, int ingId) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT id, quantity, type, unit, creation_datetime FROM stock_movement WHERE id_ingredient = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StockValue value = new StockValue(rs.getDouble("quantity"), UnitType.valueOf(rs.getString("unit")));
                StockMovement move = new StockMovement(
                        rs.getInt("id"),
                        value,
                        MovementTypeEnum.valueOf(rs.getString("type")),
                        rs.getTimestamp("creation_datetime").toInstant()
                );
                movements.add(move);
            }
        }
        return movements;
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

            if (toSave.getStockMovementList() != null && !toSave.getStockMovementList().isEmpty()) {
                String moveSql = """
            INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
             VALUES (?, ?, ?, ?::movement_type, ?::unit_type, ?)
            ON CONFLICT (id) DO NOTHING
                                """;
                try (PreparedStatement ps = conn.prepareStatement(moveSql)) {
                    for (StockMovement sm : toSave.getStockMovementList()) {
                        ps.setInt(1, sm.getId());
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

    //annexes //
    public Order saveOrder(Order orderToSave) {
        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                TableOrder tableInfo = orderToSave.getTableOrder();
                Instant creationTime = orderToSave.getCreationDatetime();

                if (!isTableAvailable(conn, tableInfo.getTable().getId(), creationTime)) {

                    List<Integer> libres = getAvailableTableNumbers(conn, creationTime);

                    String message;
                    if (libres.isEmpty()) {
                        message = "aucune table n'est disponible";
                    } else {
                        message = "les tables numéro " + libres +
                                " sont actuellement libres mais pas la numéro " +
                                tableInfo.getTable().getNumber();
                    }

                    conn.rollback();
                    throw new RuntimeException(message);
                }

                String sql = "INSERT INTO \"order\" (reference, creation_datetime, id_table) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, orderToSave.getReference());
                    ps.setTimestamp(2, Timestamp.from(creationTime));
                    ps.setInt(3, tableInfo.getTable().getId());
                    ps.executeUpdate();
                }

                conn.commit();
                return orderToSave;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isTableAvailable(Connection conn, int idTable, Instant t) throws SQLException {
        String sql = "SELECT count(*) FROM table_order WHERE id_table = ? AND ? BETWEEN arrival_datetime AND departure_datetime";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTable);
            ps.setTimestamp(2, Timestamp.from(t));
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private List<Integer> getAvailableTableNumbers(Connection conn, Instant t) throws SQLException {
        List<Integer> libres = new ArrayList<>();
        String sql = "SELECT number FROM \"table\" t WHERE NOT EXISTS (" +
                "SELECT 1 FROM table_order to2 WHERE to2.id_table = t.id " +
                "AND ? BETWEEN arrival_datetime AND departure_datetime)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.from(t));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) libres.add(rs.getInt("number"));
        }
        return libres;
    }

    public Order findOrderByReference(String reference) {
        try (Connection conn = dbConnection.getConnection()) {
            String sql = "SELECT id, reference, creation_datetime FROM \"order\" WHERE reference = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, reference);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setReference(rs.getString("reference"));
                    return order;
                } else {
                    throw new RuntimeException("Commande introuvable avec la référence : " + reference);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}