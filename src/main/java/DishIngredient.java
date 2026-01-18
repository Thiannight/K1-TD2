import java.util.Objects;

public class DishIngredient {
    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private double quantityRequired;
    private String unit;

    public DishIngredient() {}

    public DishIngredient(int id, Dish dish, Ingredient ingredient,
                          double quantityRequired, String unit) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantityRequired = quantityRequired;
        this.unit = unit;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getIngredientCost() {
        return ingredient.getPrice() * quantityRequired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return dish != null && ingredient != null &&
                dish.getId() == that.dish.getId() &&
                ingredient.getId() == that.ingredient.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(dish != null ? dish.getId() : 0,
                ingredient != null ? ingredient.getId() : 0);
    }

    @Override
    public String toString() {
        return "DishIngredient{id=" + id +
                ", dish=" + (dish != null ? dish.getName() : "null") +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                ", quantity=" + quantityRequired + " " + unit + "}";
    }
}