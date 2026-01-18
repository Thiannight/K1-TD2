import java.util.ArrayList;
import java.util.List;

public class Testmain {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            // 1. Test connexion et récupération de base
            System.out.println("1. Test de base - findDishById(1):");
            Dish dish1 = dr.findDishById(1);
            System.out.println("   ✓ " + dish1.getName() + " - " + dish1.getIngredients().size() + " ingrédients");

            // 2. Test ManyToMany
            System.out.println("\n2. Test ManyToMany - findDishIngredients(4):");
            List<DishIngredient> diList = dr.findDishIngredients(4);
            System.out.println("   ✓ " + diList.size() + " associations trouvées");
            for (DishIngredient di : diList) {
                System.out.println("     - " + di.getIngredient().getName() + ": " +
                        di.getQuantityRequired() + " " + di.getUnit());
            }

            // 3. Test création ingrédient unique
            System.out.println("\n3. Test unicité ingrédients:");
            List<Ingredient> newIngs = new ArrayList<>();
            newIngs.add(new Ingredient(0, "NouvelIngrédient", 1000.0, CategoryEnum.OTHER, null));

            try {
                List<Ingredient> created = dr.createIngredients(newIngs);
                System.out.println("   ✓ Ingrédient créé avec ID: " + created.get(0).getId());
            } catch (Exception e) {
                System.out.println("   ✗ " + e.getMessage());
            }

            // 4. Test calcul marge
            System.out.println("\n4. Test calcul marge:");
            for (int i = 1; i <= 3; i++) {
                Dish dish = dr.findDishById(i);
                System.out.println("   " + dish.getName() + ":");
                System.out.println("     Prix: " + dish.getPrice());
                System.out.println("     Coût: " + dish.getDishCost());
                if (dish.getPrice() != null) {
                    try {
                        System.out.println("     Marge: " + dish.getCrossMargin());
                    } catch (Exception e) {
                        System.out.println("     Marge: ERROR - " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}