-- Insertion des données dans la table Dish
INSERT INTO Dish (id, name, dish_type, price) VALUES
    (1, 'Salade fraîche', 'START', 3500.00),
    (2, 'Poulet grillé', 'MAIN', 12000.00),
    (3, 'Riz aux légumes', 'MAIN', NULL),
    (4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
    (5, 'Salade de fruits', 'DESSERT', NULL);

-- Insertion des données dans la table Ingredient
INSERT INTO Ingredient (id, name, price, category) VALUES
    (1, 'Laitue', 800.00, 'VEGETABLE'),
    (2, 'Tomate', 600.00, 'VEGETABLE'),
    (3, 'Poulet', 4500.00, 'ANIMAL'),
    (4, 'Chocolat', 3000.00, 'OTHER'),
    (5, 'Beurre', 2500.00, 'DAIRY');

-- Insertion des données dans la table DishIngredient
INSERT INTO DishIngredient (id, id_dish, id_ingredient, quantity_required, unit) VALUES
    (1, 1, 1, 0.20, 'KG'),
    (2, 1, 2, 0.15, 'KG'),
    (3, 2, 3, 1.00, 'KG'),
    (4, 4, 4, 0.30, 'KG'),
    (5, 4, 5, 0.20, 'KG');

-- Maj séquences
SELECT setval('dish_id_seq', (SELECT MAX(id) FROM Dish));
SELECT setval('ingredient_id_seq', (SELECT MAX(id) FROM Ingredient));
SELECT setval('dishingredient_id_seq', (SELECT MAX(id) FROM DishIngredient));