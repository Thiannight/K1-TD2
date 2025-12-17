import java.util.Objects;

public class Ingredient {
    private int id;
    private String name;
    private double price;
    private CategoryEnum category;
    private Dish dish;

    public Ingredient(){}

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public CategoryEnum getCategory() {
        return category;
    }
    public void setCategory(CategoryEnum category) {
        this.category = category;
    }
    public Dish getDish() {
        return dish;
    }
    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public String getDishName() {
        return dish != null ? dish.getName() : "Non associé à un plat";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", name='" + name + "', price=" + price +
                ", category=" + category + ", dish=" + (dish != null ? dish.getName() : "null") + "}";
    }
}
