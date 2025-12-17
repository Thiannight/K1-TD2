import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    //Récupérer un plat par son ID avec ses ingrédients

    public Dish findDishById(Integer dishID) {
        if (dishID == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }

        String dishSql = "SELECT id, name , dish_type FROM Dish WHERE id = ?";
        String ingredientsSql = "SELECT id, name ,price , category, id_dish FROM Ingredient WHERE id_dish = ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement dishStmt = conn.prepareStatement(dishSql)) {

            dishStmt.setInt(1, dishID);
            ResultSet dishRs = dishStmt.executeQuery();

            if (!dishRs.next()) {
                throw new RuntimeException("Aucun plat trouvé avec l'ID : " + dishID);
            }

            Dish dish = new Dish();
            dish.setId(dishRs.getInt("id"));
            dish.setName(dishRs.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(dishRs.getString("dish_type")));

            // Récupérer les ingrédients associés
            try (PreparedStatement ingredientsStmt = conn.prepareStatement(ingredientsSql)) {
                ingredientsStmt.setInt(1, dishID);
                ResultSet ingredientsRs = ingredientsStmt.executeQuery();

                List<Ingredient> ingredients = new ArrayList<>();
                while (ingredientsRs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientsRs.getInt("id"));
                    ingredient.setName(ingredientsRs.getString("name"));
                    ingredient.setPrice(ingredientsRs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(ingredientsRs.getString("category")));
                    ingredient.setDish(dish);
                    ingredients.add(ingredient);
                }
                dish.setIngredients(ingredients);
            }

            return dish;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat", e);
        }
    }

    // Récupération de la liste des ingrédients à travers une pagination
    public List<Ingredient> findIngredients(int page, int size) throws SQLException {

        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page et size doivent être positifs");
        }

        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.*, d.name as dish_name FROM Ingredient i " +
                "LEFT JOIN Dish d ON i.id_dish = d.id " +
                "ORDER BY i.id LIMIT ? OFFSET ?";

        int offset = (page - 1) * size;

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, size);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                ingredients.add(ingredient);
            }
        }catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e );
        }
        return ingredients;
    }

    //Pour la création de nouveaux ingrédients
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {
        if(newIngredients == null || newIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        Connection conn = null;

        try{
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            String checksql = "SELECT name FROM Ingredient WHERE name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checksql)){
                for (Ingredient ingredient : newIngredients){
                    checkStmt.setString(1, ingredient.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("L'ingrédient '" + ingredient.getName() + "' existe déjà");
                    }
                }
            }

            String insertSql = "INSERT INTO Ingredient (name, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                for  (Ingredient ingredient : newIngredients){
                    insertStmt.setString(1, ingredient.getName());
                    insertStmt.setDouble(2, ingredient.getPrice());
                    insertStmt.setString(3, ingredient.getCategory().name());
                    insertStmt.setObject(4, ingredient.getDish() != null ? ingredient.getDish().getId() : null);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if( generatedKeys.next()){
                        ingredient.setId(generatedKeys.getInt(1));
                    }
                }
            }
            conn.commit();
            return newIngredients;
        }catch (SQLException e) {
            if (conn != null){
                try{
                    conn.rollback();
                }catch (SQLException rollbackEx){
                    throw new RuntimeException("Erreur lors du rollback", rollbackEx);
                }
            }
            throw new RuntimeException("Erreur lors de la créations des ingrédients", e );
        }finally {
            if (conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException rollbackEx){
                    throw new RuntimeException("Erreur lors de la fermeture de la connexion ", rollbackEx);
                }
            }
        }
    }

}
