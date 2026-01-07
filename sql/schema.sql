-- Supprimer d'abord les tables si elles existent (dans l'ordre inverse des dépendances)
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
    id SERIAL PRIMARY KEY ,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type_enum NOT NULL,
    price NUMERIC(10, 2)
);

-- Création de la table Ingredient
CREATE TABLE Ingredient(
    id SERIAL PRIMARY KEY ,
    name VARCHAR(255) NOT NULL ,
    price NUMERIC(10, 2) NOT NULL,
    category category_enum NOT NULL,
    id_dish INT,
    CONSTRAINT fk_Dish
       FOREIGN KEY (id_dish)
           REFERENCES Dish (id)
);