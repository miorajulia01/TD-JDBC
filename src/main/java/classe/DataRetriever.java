package classe;

import config.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DataRetriever adapté au sujet TD3 : Normalisation ManyToMany
 * - Passage à ManyToMany : id_dish supprimé de Ingredient
 * - Associations via dish_ingredient (quantity_required + unit)
 * - selling_price (nullable) au lieu de price
 * - getDishCost() et getGrossMargin() calculés avec quantités
 * - Méthodes find/save adaptées (jointures + delete/insert dans dish_ingredient)
 */
public class DataRetriever {

    // ────────────────────────────────────────────────────────────────
    // findDishById : Charge plat + associations ManyToMany
    // CHANGEMENT : jointure dish → dish_ingredient → ingredient
    // INCHANGÉ : gestion NULL pour price, recharge complet
    // ────────────────────────────────────────────────────────────────
    public Dish findDishById(Integer id) {
        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type, d.price,
                   di.id AS di_id, di.quantity_required, di.unit,
                   i.id AS ing_id, i.name AS ing_name, i.price AS ing_price, i.category
            FROM dish d
            LEFT JOIN dish_ingredient di ON di.id_dish = d.id
            LEFT JOIN ingredient i ON i.id = di.id_ingredient
            WHERE d.id = ?
            ORDER BY di.id
            """;

        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Dish dish = null;
            List<DishIngredient> associations = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish();
                    dish.setId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("dish_name"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    // CHANGÉ : dish.price au lieu de dish.selling_price
                    dish.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));
                }

                if (rs.getObject("di_id") != null) {
                    DishIngredient di = new DishIngredient();
                    di.setId(rs.getInt("di_id"));
                    di.setIdDish(id);
                    di.setIdIngredient(rs.getInt("ing_id"));
                    di.setQuantityRequired(rs.getDouble("quantity_required"));
                    di.setUnit(UnitType.valueOf(rs.getString("unit")));

                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("ing_id"));
                    ing.setName(rs.getString("ing_name"));
                    ing.setPrice(rs.getDouble("ing_price"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                    di.setIngredient(ing);
                    associations.add(di);
                }
            }

            if (dish == null) {
                throw new RuntimeException("Dish not found: " + id);
            }

            dish.setDishIngredients(associations);
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lecture plat " + id, e);
        }
    }


    // ────────────────────────────────────────────────────────────────
    // saveDish : UPSERT plat + gestion ManyToMany dans dish_ingredient
    // CHANGEMENT : DELETE puis INSERT dans dish_ingredient (au lieu d'update id_dish)
    // INCHANGÉ : UPSERT PostgreSQL, gestion selling_price nullable, sequences manuelles
    // ────────────────────────────────────────────────────────────────
    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
            INSERT INTO dish (id, name, dish_type, selling_price)
            VALUES (?, ?, ?::dish_type, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                selling_price = EXCLUDED.selling_price
            RETURNING id
            """;

        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());
                if (toSave.getPrice() != null) {
                    ps.setDouble(4, toSave.getPrice());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            // ────────────────────────────────────────────────────────────────
            // CHANGEMENT : ManyToMany → on supprime TOUTES les anciennes associations
            // (pas de detach sélectif comme prof, car plus simple avec delete)
            // ────────────────────────────────────────────────────────────────
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }

            // ────────────────────────────────────────────────────────────────
            // CHANGEMENT : INSERT dans dish_ingredient pour nouvelles associations
            // ────────────────────────────────────────────────────────────────
            List<DishIngredient> associations = toSave.getDishIngredients();
            if (associations != null && !associations.isEmpty()) {
                String insertSql = """
                    INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
                    VALUES (?, ?, ?, ?::unit_type)
                    """;
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    for (DishIngredient di : associations) {
                        ps.setInt(1, dishId);
                        ps.setInt(2, di.getIdIngredient());
                        ps.setDouble(3, di.getQuantityRequired());
                        ps.setString(4, di.getUnit().name());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur saveDish", e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> saved = new ArrayList<>();
        DBConnection db = new DBConnection();
        Connection conn = db.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                INSERT INTO ingredient (id, name, category, price)
                VALUES (?, ?, ?::ingredient_category, ?)
                RETURNING id
                """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ing : newIngredients) {
                    if (ing.getId() != null) {
                        ps.setInt(1, ing.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ing.getName());
                    ps.setString(3, ing.getCategory().name());
                    ps.setDouble(4, ing.getPrice());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        ing.setId(rs.getInt(1));
                        saved.add(ing);
                    }
                }
            }
            conn.commit();
            return saved;
        } catch (SQLException e) {
            conn.rollback();
            throw new RuntimeException(e);
        } finally {
            db.closeConnection(conn);
        }
    }

    // ────────────────────────────────────────────────────────────────
    // Méthodes privées du prof (inchangées) : gestion sequences PostgreSQL
    // Utiles pour IDs manuels avant INSERT
    // ────────────────────────────────────────────────────────────────
    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException("No sequence for " + tableName + "." + columnName);
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);
        String nextValSql = "SELECT nextval(?)";
        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );
        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}