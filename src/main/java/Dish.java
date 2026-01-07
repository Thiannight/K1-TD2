import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private Double price;
    private List<Ingredient> ingredients;

    public Dish() {
        this.ingredients = new ArrayList<>();
    }

    public Dish(int id, String name, DishTypeEnum dishType, Double price, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = Objects.requireNonNullElseGet(ingredients, ArrayList::new);
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

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
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

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Double getCrossMargin() {
        if (this.price == null) {
            throw new RuntimeException("Impossible de calculer la marge : le prix de vente n'a pas été fixé pour le plat '" + this.name + "'");
        }
        Double cost = getDishCost();
        return this.price - cost;
    }

    @Override
    public String toString() {
        return "Dish{id=" + id + ", name='" + name + "', dishType=" + dishType +
                ", price=" + price + ", cost=" + getDishCost() +
                ", margin=" + (price != null ? getCrossMargin() : "N/A") +
                ", ingredients=" + ingredients + "}";
    }
}