-- Insertion des données dans la table Dish
INSERT INTO Dish (id, name, dish_type, price) VALUES
    (1, 'Salade fraîche', 'START', 2000.00),
    (2, 'Poulet grillé', 'MAIN', 6000.00),
    (3, 'Riz aux légumes', 'MAIN', NULL),
    (4, 'Gâteau au chocolat', 'DESSERT', NULL),
    (5, 'Salade de fruits', 'DESSERT', NULL);

-- Insertion des données dans la table Ingredient
INSERT INTO Ingredient (id, name, price, category, id_dish) VALUES
    (1, 'Laitue', 800.00, 'VEGETABLE', 1),
    (2, 'Tomate', 600.00, 'VEGETABLE', 1),
    (3, 'Poulet', 4500.00, 'ANIMAL', 2),
    (4, 'Chocolat', 3000.00, 'OTHER', 4),
    (5, 'Beurre', 2500.00, 'DAIRY', 4);


SELECT setval('dish_id_seq' , (SELECT MAX(id) FROM Dish));
SELECT setval('ingredient_id_seq', (SELECT MAX(id) FROM Ingredient));