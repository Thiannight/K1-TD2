import java.util.List;

public class Testmain {
    public static void main(String[] args) {
        System.out.println("=== Début des tests ===");

        DataRetriever dr = new DataRetriever();

        try {
            // Test a
            System.out.println("\nTest a - findDishById(1):");
            Dish d1 = dr.findDishById(1);
            System.out.println("Plat: " + d1.getName());
            System.out.println("Ingrédients: " + d1.getIngredients().size());

            // Test b
            System.out.println("\nTest b - findIngredients(2, 2):");
            List<Ingredient> ingPage2 = dr.findIngredients(2, 2);
            System.out.println("Résultats: " + ingPage2.size());

            // Test e
            System.out.println("\nTest e - findDishsByIngredientName('eur'):");
            List<Dish> dishes = dr.findDishsByIngredientName("eur");
            System.out.println("Plats trouvés: " + dishes.size());

            // Test f
            System.out.println("\nTest f - findIngredientsByCriteria:");
            List<Ingredient> veggies = dr.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10);
            System.out.println("Légumes trouvés: " + veggies.size());

            System.out.println("\n=== Tous les tests passés! ===");

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}