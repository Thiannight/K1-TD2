import java.util.ArrayList;
import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish() {
        this.ingredients = new ArrayList<>();
    }

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = new ArrayList<>();
    }

    // Getters et setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }
    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        if (ingredients == null) {
            ingredients = new ArrayList<>();
        }
        ingredients.add(ingredient);
        ingredient.setDish(this);
    }

    public Double getDishPrice() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    @Override
    public String toString() {
        return "Dish{id=" + id + ", name='" + name + "', dishType=" + dishType +
                ", price=" + getDishPrice() + ", ingredients=" + ingredients + "}";
    }
}
