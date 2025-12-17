package classe;

import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DataRetriever {
    private DBConnection dbConnection;
        public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    public Dish findDishById(Integer id) {
        throw new RuntimeException("Not implemented yet");

        String sql = "";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
               Dish dish = new Dish();
               dish.setId(rs.getInt("id"));
               dish.setName(rs.getString("name"));
               dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            }
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Ingredient> findIngredientsByDishId ( int page, int size){
        throw new RuntimeException("Not implemented yet");
    }

    public List<Ingredient> createIngredients ( List<Ingredient> newIngredients){
        throw new RuntimeException("Not implemented yet");
    }
}
