public class MainTest {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            // Test 1: Récupération d'un plat avec prix (Salade fraîche)
            System.out.println("Test 1 - findDishById(1) - Plat avec prix:");
            Dish dish1 = dr.findDishById(1);
            System.out.println("   Plat: " + dish1.getName());
            System.out.println("   Prix de vente: " + dish1.getPrice());
            System.out.println("   Coût des ingrédients: " + dish1.getDishCost());

            try {
                Double margin = dish1.getCrossMargin();
                System.out.println("   Marge brute: " + margin);
                System.out.println("   Calcul: " + dish1.getPrice() + " - " + dish1.getDishCost() + " = " + margin);
            } catch (RuntimeException e) {
                System.out.println("   Exception: " + e.getMessage());
            }
            System.out.println();

            // Test 2: Récupération d'un plat sans prix (Riz aux légumes)
            System.out.println("Test 2 - findDishById(3) - Plat sans prix:");
            Dish dish3 = dr.findDishById(3);
            System.out.println("   Plat: " + dish3.getName());
            System.out.println("   Prix de vente: " + dish3.getPrice());
            System.out.println("   Coût des ingrédients: " + dish3.getDishCost());

            try {
                Double margin = dish3.getCrossMargin();
                System.out.println("   Marge brute: " + margin);
            } catch (RuntimeException e) {
                System.out.println("   Exception attendue: " + e.getMessage());
            }
            System.out.println();

            // Test 3: Sauvegarde d'un nouveau plat avec prix
            System.out.println("Test 3 - saveDish() - Création nouveau plat:");
            Dish newDish = new Dish();
            newDish.setName("Pâtes carbonara");
            newDish.setDishType(DishTypeEnum.MAIN);
            newDish.setPrice(4500.00);

            Dish savedDish = dr.saveDish(newDish);
            System.out.println("   Plat créé avec ID: " + savedDish.getId());
            System.out.println("   Nom: " + savedDish.getName());
            System.out.println("   Prix: " + savedDish.getPrice());
            System.out.println();

            // Test 4: Mise à jour du prix d'un plat existant
            System.out.println("Test 4 - saveDish() - Mise à jour prix:");
            Dish dishToUpdate = dr.findDishById(4);  // Gâteau au chocolat
            System.out.println("   Avant - Prix: " + dishToUpdate.getPrice());

            dishToUpdate.setPrice(3500.00);
            Dish updatedDish = dr.saveDish(dishToUpdate);
            System.out.println("   Après - Prix: " + updatedDish.getPrice());

            try {
                Double margin = updatedDish.getCrossMargin();
                System.out.println("   Marge brute: " + margin);
                System.out.println("   Coût ingrédients: " + updatedDish.getDishCost());
            } catch (RuntimeException e) {
                System.out.println("   Exception: " + e.getMessage());
            }
            System.out.println();

            // Test 5: Test complet avec tous les plats
            System.out.println("Test 5 - Analyse de tous les plats:");
            for (int i = 1; i <= 5; i++) {
                try {
                    Dish dish = dr.findDishById(i);
                    System.out.println("\n   Plat " + i + ": " + dish.getName());
                    System.out.println("   Prix: " + dish.getPrice());
                    System.out.println("   Coût: " + dish.getDishCost());

                    if (dish.getPrice() != null) {
                        System.out.println("   Marge: " + dish.getCrossMargin());
                    } else {
                        System.out.println("   Marge: Non calculable (prix non défini)");
                    }
                } catch (Exception e) {
                    System.out.println("   Erreur: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}