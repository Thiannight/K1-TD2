// DataRetriever.java (version mise à jour)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection;

    public DataRetriever() {
        this.dbConnection = new DBConnection();
    }

    // a) Récupérer un plat par son ID avec ses ingrédients (version ManyToMany)
    public Dish findDishById(Integer dishID) {
        if (dishID == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }

        String dishSql = "SELECT id, name, dish_type, price FROM Dish WHERE id = ?";
        String dishIngredientsSql =
                "SELECT di.id, di.quantity_required, di.unit, " +
                        "i.id as ingredient_id, i.name as ingredient_name, i.price as ingredient_price, i.category " +
                        "FROM DishIngredient di " +
                        "JOIN Ingredient i ON di.id_ingredient = i.id " +
                        "WHERE di.id_dish = ?";

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

            double priceValue = dishRs.getDouble("price");
            if (!dishRs.wasNull()) {
                dish.setPrice(priceValue);
            }

            try (PreparedStatement diStmt = conn.prepareStatement(dishIngredientsSql)) {
                diStmt.setInt(1, dishID);
                ResultSet diRs = diStmt.executeQuery();

                List<Ingredient> ingredients = new ArrayList<>();
                while (diRs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(diRs.getInt("ingredient_id"));
                    ingredient.setName(diRs.getString("ingredient_name"));
                    ingredient.setPrice(diRs.getDouble("ingredient_price"));
                    ingredient.setCategory(CategoryEnum.valueOf(diRs.getString("category")));


                    ingredients.add(ingredient);
                }
                dish.setIngredients(ingredients);
            }

            return dish;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat", e);
        }
    }

    // b) Récupération de la liste des ingrédients (inchangée)
    public List<Ingredient> findIngredients(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Page et size doivent être positifs");
        }

        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT i.* FROM Ingredient i ORDER BY i.id LIMIT ? OFFSET ?";

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
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }
        return ingredients;
    }

    // c) Pour la création de nouveaux ingrédients (simplifié)
    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        Connection conn = null;

        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT name FROM Ingredient WHERE name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                for (Ingredient ingredient : newIngredients) {
                    checkStmt.setString(1, ingredient.getName());
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        conn.rollback();
                        throw new RuntimeException("L'ingrédient '" + ingredient.getName() + "' existe déjà");
                    }
                }
            }

            String insertSql = "INSERT INTO Ingredient (name, price, category) VALUES (?, ?, ?::category_enum)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                for (Ingredient ingredient : newIngredients) {
                    insertStmt.setString(1, ingredient.getName());
                    insertStmt.setDouble(2, ingredient.getPrice());
                    insertStmt.setString(3, ingredient.getCategory().name());

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

    // d) Méthode saveDish - mise à jour avec gestion ManyToMany
    public Dish saveDish(Dish dishToSave) {
        Connection conn = null;
        try {
            conn = dbConnection.getDBConnection();
            conn.setAutoCommit(false);

            Integer dishId = dishToSave.getId();
            if (dishId == null || dishId == 0) {
                String insertSql = "INSERT INTO Dish (name, dish_type, price) VALUES (?, ?::dish_type_enum, ?) RETURNING id";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishType().name());

                    if (dishToSave.getPrice() != null) {
                        stmt.setDouble(3, dishToSave.getPrice());
                    } else {
                        stmt.setNull(3, Types.DOUBLE);
                    }

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        dishId = rs.getInt(1);
                        dishToSave.setId(dishId);
                    }
                }
            } else {
                String updateSql = "UPDATE Dish SET name = ?, dish_type = ?::dish_type_enum, price = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, dishToSave.getName());
                    stmt.setString(2, dishToSave.getDishType().name());

                    if (dishToSave.getPrice() != null) {
                        stmt.setDouble(3, dishToSave.getPrice());
                    } else {
                        stmt.setNull(3, Types.DOUBLE);
                    }

                    stmt.setInt(4, dishId);
                    stmt.executeUpdate();
                }
                String deleteAssociationsSql = "DELETE FROM DishIngredient WHERE id_dish = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteAssociationsSql)) {
                    stmt.setInt(1, dishId);
                    stmt.executeUpdate();
                }
            }

            if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                String associateSql = "INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(associateSql)) {
                    for (Ingredient ingredient : dishToSave.getIngredients()) {
                        if (ingredient.getId() > 0) {
                            stmt.setInt(1, dishId);
                            stmt.setInt(2, ingredient.getId());
                            // Valeurs par défaut pour quantity_required et unit
                            stmt.setDouble(3, 1.0); // Quantité par défaut
                            stmt.setString(4, "KG"); // Unité par défaut
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

    public List<DishIngredient> findDishIngredients(Integer dishId) {
        if (dishId == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }

        List<DishIngredient> dishIngredients = new ArrayList<>();
        String sql =
                "SELECT di.id, di.quantity_required, di.unit, " +
                        "d.id as dish_id, d.name as dish_name, d.dish_type, d.price as dish_price, " +
                        "i.id as ingredient_id, i.name as ingredient_name, i.price as ingredient_price, i.category " +
                        "FROM DishIngredient di " +
                        "JOIN Dish d ON di.id_dish = d.id " +
                        "JOIN Ingredient i ON di.id_ingredient = i.id " +
                        "WHERE d.id = ?";

        try (Connection conn = dbConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setId(rs.getInt("id"));
                dishIngredient.setQuantityRequired(rs.getDouble("quantity_required"));
                dishIngredient.setUnit(rs.getString("unit"));

                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                double dishPrice = rs.getDouble("dish_price");
                if (!rs.wasNull()) {
                    dish.setPrice(dishPrice);
                }
                dishIngredient.setDish(dish);

                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("ingredient_id"));
                ingredient.setName(rs.getString("ingredient_name"));
                ingredient.setPrice(rs.getDouble("ingredient_price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                dishIngredient.setIngredient(ingredient);

                dishIngredients.add(dishIngredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients du plat", e);
        }
        return dishIngredients;
    }
}
//test github