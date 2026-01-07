import java.util.ArrayList;
import java.util.List;

public class Testmain {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        try {
            // Test a: findDishById(1)
            System.out.println("Test a) Dish findDishById(1):");
            Dish dish1 = dr.findDishById(1);
            System.out.println("   Résultat: Plat = " + dish1.getName());
            System.out.println("   Ingrédients = " + dish1.getIngredients().size());
            System.out.println("   Détail: " + dish1.getIngredients());
            System.out.println("   Attendu: Salade Fraîche avec 2 ingrédients (Laitue et Tomate)");

            // Test b: findDishById(999)
            System.out.println("\nTest b) Dish findDishById(999):");
            try {
                Dish dish999 = dr.findDishById(999);
                System.out.println("   ERREUR: Aucune exception levée!");
            } catch (RuntimeException e) {
                System.out.println("   Résultat: RuntimeException levée");
                System.out.println("   Message: " + e.getMessage());
                System.out.println("   Attendu: RuntimeException");
            }

            // Test c: findIngredients(2, 2)
            System.out.println("\nTest c) List<Ingredient> findIngredients(2, 2):");
            List<Ingredient> ingredientsPage2 = dr.findIngredients(2, 2);
            System.out.println("   Résultat: " + ingredientsPage2.size() + " ingrédients");
            System.out.println("   Liste: " + ingredientsPage2);
            System.out.println("   Attendu: 2 ingrédients (Poulet, Chocolat)");

            // Test d: findIngredients(3, 5)
            System.out.println("\nTest d) List<Ingredient> findIngredients(3, 5):");
            List<Ingredient> ingredientsPage3 = dr.findIngredients(3, 5);
            System.out.println("   Résultat: " + ingredientsPage3.size() + " ingrédients");
            System.out.println("   Liste: " + ingredientsPage3);
            System.out.println("   Attendu: Liste vide");

            // Test e: findDishsByIngredientName("eur")
            System.out.println("\nTest e) List<Dish> findDishsByIngredientName(\"eur\"):");
            List<Dish> dishesEur = dr.findDishsByIngredientName("eur");
            System.out.println("   Résultat: " + dishesEur.size() + " plat(s)");
            System.out.println("   Liste: " + dishesEur);
            System.out.println("   Attendu: 1 plat (Gâteau au chocolat)");

            // Test f: findIngredientsByCriteria avec category=VEGETABLE
            System.out.println("\nTest f) List<Ingredient> findIngredientsByCriteria(...):");
            System.out.println("   Paramètres: ingredientName=null, category=VEGETABLE, dishName=null, page=1, size=10");
            List<Ingredient> veggies = dr.findIngredientsByCriteria(null, CategoryEnum.VEGETABLE, null, 1, 10);
            System.out.println("   Résultat: " + veggies.size() + " ingrédient(s)");
            System.out.println("   Liste: " + veggies);
            System.out.println("   Attendu: 2 ingrédients (Laitue, Tomate)");

            // Test g: findIngredientsByCriteria avec ingredientName="cho" et dishName="Sal"
            System.out.println("\nTest g) List<Ingredient> findIngredientsByCriteria(...):");
            System.out.println("   Paramètres: ingredientName=\"cho\", category=null, dishName=\"Sal\", page=1, size=10");
            List<Ingredient> choSal = dr.findIngredientsByCriteria("cho", null, "Sal", 1, 10);
            System.out.println("   Résultat: " + choSal.size() + " ingrédient(s)");
            System.out.println("   Liste: " + choSal);
            System.out.println("   Attendu: Liste vide");

            // Test h: findIngredientsByCriteria avec ingredientName="cho" et dishName="gâteau"
            System.out.println("\nTest h) List<Ingredient> findIngredientsByCriteria(...):");
            System.out.println("   Paramètres: ingredientName=\"cho\", category=null, dishName=\"gâteau\", page=1, size=10");
            List<Ingredient> choGateau = dr.findIngredientsByCriteria("cho", null, "gâteau", 1, 10);
            System.out.println("   Résultat: " + choGateau.size() + " ingrédient(s)");
            System.out.println("   Liste: " + choGateau);
            System.out.println("   Attendu: 1 ingrédient (Chocolat)");

            // Test i: createIngredients avec Fromage et Oignon
            System.out.println("\nTest i) List<Ingredient> createIngredients(...):");
            System.out.println("   Paramètres: [Fromage (DAIRY, 1200.0), Oignon (VEGETABLE, 500.0)]");
            List<Ingredient> newIngredients1 = new ArrayList<>();
            newIngredients1.add(new Ingredient(0, "Fromage", 1200.0, CategoryEnum.DAIRY, null));
            newIngredients1.add(new Ingredient(0, "Oignon", 500.0, CategoryEnum.VEGETABLE, null));

            try {
                List<Ingredient> created1 = dr.createIngredients(newIngredients1);
                System.out.println("   Résultat: " + created1.size() + " ingrédient(s) créé(s)");
                System.out.println("   Liste: " + created1);
                System.out.println("   Attendu: 2 ingrédients créés (Fromage, Oignon)");
            } catch (RuntimeException e) {
                System.out.println("   Exception: " + e.getMessage());
            }

            // Test j: createIngredients avec Carotte et Laitue (Laitue existe déjà)
            System.out.println("\nTest j) List<Ingredient> createIngredients(...):");
            System.out.println("   Paramètres: [Carotte (VEGETABLE, 2000.0), Laitue (VEGETABLE, 2000.0)]");
            List<Ingredient> newIngredients2 = new ArrayList<>();
            newIngredients2.add(new Ingredient(0, "Carotte", 2000.0, CategoryEnum.VEGETABLE, null));
            newIngredients2.add(new Ingredient(0, "Laitue", 2000.0, CategoryEnum.VEGETABLE, null));

            try {
                List<Ingredient> created2 = dr.createIngredients(newIngredients2);
                System.out.println("   Résultat: " + created2.size() + " ingrédient(s) créé(s)");
                System.out.println("   ERREUR: Aucune exception levée!");
            } catch (RuntimeException e) {
                System.out.println("   Résultat: Exception levée");
                System.out.println("   Message: " + e.getMessage());
                System.out.println("   Attendu: RuntimeException car Laitue existe déjà");
            }

            // Test k: saveDish - création nouveau plat
            System.out.println("\nTest k) Dish saveDish(...):");
            System.out.println("   Paramètres: Dish(name=Soupe de légumes, dishType=START, ingrédients=[Oignon])");
            Dish newDish = new Dish();
            newDish.setName("Soupe de légumes");
            newDish.setDishType(DishTypeEnum.START);

            List<Ingredient> oignonList = dr.findIngredientsByCriteria("Oignon", null, null, 1, 1);
            if (!oignonList.isEmpty()) {
                List<Ingredient> ingredientsForDish = new ArrayList<>();
                ingredientsForDish.add(oignonList.get(0));
                newDish.setIngredients(ingredientsForDish);
            }

            Dish savedDish1 = dr.saveDish(newDish);
            System.out.println("   Résultat: Plat créé avec id=" + savedDish1.getId());
            System.out.println("   Nom: " + savedDish1.getName());
            System.out.println("   Nombre d'ingrédients: " + savedDish1.getIngredients().size());
            System.out.println("   Attendu: Plat créé avec Oignon");

            // Test l: saveDish - mise à jour plat existant avec ajout d'ingrédients
            System.out.println("\nTest l) Dish saveDish(...):");
            System.out.println("   Paramètres: Dish(id=1, name=Salade fraîche, dishType=START, ingrédients=[Oignon, Laitue, Tomate, Fromage])");

            Dish dishToUpdate = dr.findDishById(1);
            dishToUpdate.setName("Salade fraîche");
            dishToUpdate.setDishType(DishTypeEnum.START);

            List<Ingredient> allIngredients = new ArrayList<>();

            List<Ingredient> oignonSearch = dr.findIngredientsByCriteria("Oignon", null, null, 1, 1);
            if (!oignonSearch.isEmpty()) allIngredients.add(oignonSearch.get(0));

            List<Ingredient> laitueSearch = dr.findIngredientsByCriteria("Laitue", null, null, 1, 1);
            if (!laitueSearch.isEmpty()) allIngredients.add(laitueSearch.get(0));

            List<Ingredient> tomateSearch = dr.findIngredientsByCriteria("Tomate", null, null, 1, 1);
            if (!tomateSearch.isEmpty()) allIngredients.add(tomateSearch.get(0));

            List<Ingredient> fromageSearch = dr.findIngredientsByCriteria("Fromage", null, null, 1, 1);
            if (!fromageSearch.isEmpty()) allIngredients.add(fromageSearch.get(0));

            dishToUpdate.setIngredients(allIngredients);

            Dish updatedDish1 = dr.saveDish(dishToUpdate);
            System.out.println("   Résultat: Plat mis à jour id=" + updatedDish1.getId());
            System.out.println("   Nombre d'ingrédients: " + updatedDish1.getIngredients().size());
            System.out.println("   Attendu: Plat avec 4 ingrédients");

            // Test m: saveDish - mise à jour avec suppression d'ingrédients
            System.out.println("\nTest m) Dish saveDish(...):");
            System.out.println("   Paramètres: Dish(id=1, name=Salade de fromage, dishType=START, ingrédients=[Fromage])");

            Dish dishToUpdate2 = new Dish();
            dishToUpdate2.setId(1);
            dishToUpdate2.setName("Salade de fromage");
            dishToUpdate2.setDishType(DishTypeEnum.START);

            List<Ingredient> singleIngredient = new ArrayList<>();
            if (!fromageSearch.isEmpty()) {
                singleIngredient.add(fromageSearch.get(0));
            }
            dishToUpdate2.setIngredients(singleIngredient);

            Dish updatedDish2 = dr.saveDish(dishToUpdate2);
            System.out.println("   Résultat: Plat mis à jour id=" + updatedDish2.getId());
            System.out.println("   Nom: " + updatedDish2.getName());
            System.out.println("   Nombre d'ingrédients: " + updatedDish2.getIngredients().size());
            System.out.println("   Attendu: Plat renommé avec seulement Fromage");

        } catch (Exception e) {
            System.err.println("Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}