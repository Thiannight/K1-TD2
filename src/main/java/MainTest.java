import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();


        try {
            // Test 1: Vérifier que la base fonctionne
            System.out.println("Test 1 - Vérification de la connexion:");
            Dish dish1 = dr.findDishById(1);
            System.out.println("✓ Plat trouvé: " + dish1.getName());

            // Test 2: Calcul des coûts
            System.out.println("\nTest 2 - Calcul des coûts:");
            for (int i = 1; i <= 5; i++) {
                Dish dish = dr.findDishById(i);
                System.out.println("Plat " + i + " - " + dish.getName() + ":");
                System.out.println("  Prix: " + dish.getPrice());
                System.out.println("  Coût ingrédients: " + dish.getDishCost());

                if (dish.getPrice() != null) {
                    try {
                        System.out.println("  Marge: " + dish.getCrossMargin());
                    } catch (Exception e) {
                        System.out.println("  Marge: Erreur - " + e.getMessage());
                    }
                }
                System.out.println();
            }

            // Test 3: Vérification ManyToMany
            System.out.println("\nTest 3 - Vérification ManyToMany:");
            System.out.println("Ingrédients disponibles:");
            List<Ingredient> ingredients = dr.findIngredients(1, 10);
            for (Ingredient ing : ingredients) {
                System.out.println("  - " + ing.getName() + " (" + ing.getCategory() + ")");
            }

        } catch (Exception e) {
            System.err.println("ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}