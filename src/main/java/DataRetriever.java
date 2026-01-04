import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    // a) Récupérer un plat par son ID avec ses ingrédients
    public Dish findDishById(Integer dishID) {
        if (dishID == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }

        String dishSql = "SELECT id, name, dish_type FROM Dish WHERE id = ?";
        String ingredientsSql = "SELECT id, name, price, category, id_dish FROM Ingredient WHERE id_dish = ?";

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

    // b) Récupération de la liste des ingrédients à travers une pagination
    public List<Ingredient> findIngredients(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page et size doivent être positifs");
        }

        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.*, d.name as dish_name FROM Ingredient i " +
                "LEFT JOIN Dish d ON i.id_dish = d.id " +
                "ORDER BY i.id LIMIT ? OFFSET ?";

        int offset = (page - 1) * size;

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, size);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                String dishName = rs.getString("dish_name");
                if (dishName != null) {
                    Dish dish = new Dish();
                    dish.setName(dishName);
                    ingredient.setDish(dish);
                }

                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }
        return ingredients;
    }

    // c) Pour la création de nouveaux ingrédients - CORRECTION ICI
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        Connection conn = null;

        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            // Vérifier si les ingrédients existent déjà
            String checksql = "SELECT name FROM Ingredient WHERE name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checksql)) {
                for (Ingredient ingredient : newIngredients) {
                    checkStmt.setString(1, ingredient.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("L'ingrédient '" + ingredient.getName() + "' existe déjà");
                    }
                }
            }

            String insertSql = "INSERT INTO Ingredient (name, price, category, id_dish) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                for (Ingredient ingredient : newIngredients) {
                    insertStmt.setString(1, ingredient.getName());
                    insertStmt.setDouble(2, ingredient.getPrice());
                    insertStmt.setString(3, ingredient.getCategory().name());

                    if (ingredient.getDish() != null && ingredient.getDish().getId() > 0) {
                        insertStmt.setInt(4, ingredient.getDish().getId());
                    } else {
                        insertStmt.setNull(4, Types.INTEGER);
                    }

                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        ingredient.setId(generatedKeys.getInt(1));
                    }
                }
            }
            conn.commit();
            return new ArrayList<>(newIngredients);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Erreur lors du rollback", rollbackEx);
                }
            }
            throw new RuntimeException("Erreur lors de la création des ingrédients", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + closeEx.getMessage());
                }
            }
        }
    }

    // d) Méthode saveDish manquante
    public Dish saveDish(Dish dishToSave) {
        Connection conn = null;
        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            Integer dishId = dishToSave.getId();

            if (dishId == null || dishId == 0) {
                String insertSql = "INSERT INTO Dish (name, dish_type) VALUES (?, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishType().name());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        dishId = rs.getInt(1);
                        dishToSave.setId(dishId);
                    }
                }
            } else {
                String updateSql = "UPDATE Dish SET name = ?, dish_type = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishType().name());
                    stmt.setInt(3, dishId);
                    stmt.executeUpdate();
                }

                String dissociateSql = "UPDATE Ingredient SET id_dish = NULL WHERE id_dish = ?";
                try (PreparedStatement stmt = conn.prepareStatement(dissociateSql)) {
                    stmt.setInt(1, dishId);
                    stmt.executeUpdate();
                }
            }

            if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                String associateSql = "UPDATE Ingredient SET id_dish = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(associateSql)) {
                    for (Ingredient ingredient : dishToSave.getIngredients()) {
                        if (ingredient.getId() > 0) {
                            stmt.setInt(1, dishId);
                            stmt.setInt(2, ingredient.getId());
                            stmt.addBatch();
                        }
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            throw new RuntimeException("Erreur lors de la sauvegarde du plat", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    // e) Méthode findDishsByIngredientName manquante
    public List<Dish> findDishsByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT DISTINCT d.id, d.name, d.dish_type FROM Dish d " +
                "JOIN Ingredient i ON d.id = i.id_dish " +
                "WHERE LOWER(i.name) LIKE LOWER(?)";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + ingredientName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des plats", e);
        }

        return dishes;
    }

    // f) Méthode findIngredientsByCriteria manquante
    public List<Ingredient> findIngredientsByCriteria(String ingredientName, CategoryEnum category,
                                                      String dishName, int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page et size doivent être positifs");
        }

        List<Ingredient> ingredients = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.*, d.name as dish_name FROM Ingredient i " +
                        "LEFT JOIN Dish d ON i.id_dish = d.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (ingredientName != null && !ingredientName.trim().isEmpty()) {
            sql.append(" AND LOWER(i.name) LIKE LOWER(?)");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?::category_enum");
            params.add(category.name());
        }

        if (dishName != null && !dishName.trim().isEmpty()) {
            sql.append(" AND LOWER(d.name) LIKE LOWER(?)");
            params.add("%" + dishName + "%");
        }

        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");
        int offset = (page - 1) * size;
        params.add(size);
        params.add(offset);

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                if (rs.getString("dish_name") != null) {
                    Dish dish = new Dish();
                    dish.setName(rs.getString("dish_name"));
                    ingredient.setDish(dish);
                }

                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ingrédients", e);
        }

        return ingredients;
    }
}