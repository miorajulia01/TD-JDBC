INSERT INTO dish (id, name, dish_type, price) VALUES
(1, 'Salade fraîche', 'STARTER', 3500.00),
(2, 'Poulet grillé', 'MAIN', 12000.00),
(3, 'Riz aux légumes', 'MAIN', NULL),
(4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
(5, 'Salade de fruits', 'DESSERT', NULL);


INSERT INTO Ingredient (id, name, price, category, id_dish) VALUES
(1, 'Laitue', 800.00, VEGETABLE, 1 ),
(2, 'Tomate', 600.00,VEGETABLE, 1),
(3, 'Poulet', 4500.00,ANIMAL, 2),
(4, 'Chocolat', 3000.00,OTHER, 4),
(5, 'Beurre', 2500.00,DAIRY, 4);


INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
(1, 1, 0.20, 'KG'),
(1, 2, 0.15, 'KG'),
(2, 3, 1.00, 'KG'),
(4, 4, 0.30, 'KG'),
(4, 5, 0.20, 'KG');


SELECT d.id,
       d.name,
       COALESCE(SUM(i.price * di.quantity_required), 0) AS price
FROM dish d
         LEFT JOIN dish_ingredient di ON di.id_dish = d.id
         LEFT JOIN ingredient i ON i.id = di.id_ingredient
GROUP BY d.id;



