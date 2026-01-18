-- Supprimerles tables si elles existent
DROP TABLE IF EXISTS DishIngredient;
DROP TABLE IF EXISTS Ingredient;
DROP TABLE IF EXISTS Dish;

-- Supprimer les types ENUM s'ils existent
DROP TYPE IF EXISTS dish_type_enum CASCADE;
DROP TYPE IF EXISTS category_enum CASCADE;

-- Création des types ENUM
CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

-- Création de la table Dish
CREATE TABLE Dish(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type_enum NOT NULL,
    price NUMERIC(10, 2)
);

-- Création de la table Ingredient
CREATE TABLE Ingredient(
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    category category_enum NOT NULL
);

-- Création de la table de jointure
CREATE TABLE DishIngredient(
    id SERIAL PRIMARY KEY,
    id_dish INT NOT NULL,
    id_ingredient INT NOT NULL,
    quantity_required NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(50) NOT NULL,

    CONSTRAINT fk_dish
    FOREIGN KEY (id_dish)
       REFERENCES Dish(id)
       ON DELETE CASCADE,

    CONSTRAINT fk_ingredient
    FOREIGN KEY (id_ingredient)
       REFERENCES Ingredient(id)
       ON DELETE CASCADE,

    CONSTRAINT unique_dish_ingredient
    UNIQUE (id_dish, id_ingredient)
);