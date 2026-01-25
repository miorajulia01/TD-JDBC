INSERT INTO dish (id, name, dish_type, price) VALUES
(1, 'Salade fraîche', 'START', 3500.00),
(2, 'Poulet grillé', 'MAIN', 12000.00),
(3, 'Riz aux légumes', 'MAIN', NULL),
(4, 'Gâteau au chocolat', 'DESSERT', 8000.00),
(5, 'Salade de fruits', 'DESSERT', NULL);


INSERT INTO ingredient (id, name, price, category) VALUES
(1, 'Laitue', 800.00, 'VEGETABLE' ),
(2, 'Tomate', 600.00,'VEGETABLE'),
(3, 'Poulet', 4500.00,'ANIMAL'),
(4, 'Chocolat', 3000.00,'OTHER'),
(5, 'Beurre', 2500.00,'DAIRY');


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

INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime) VALUES
    (1, 1, 5.0, 'IN', 'KG', '2024-01-05 08:00:00'),
    (2, 1, 0.2, 'OUT', 'KG', '2024-01-06 12:00:00'),
    (3, 2, 4.0, 'IN', 'KG', '2024-01-05 08:00:00'),
    (4, 2, 0.15, 'OUT', 'KG', '2024-01-06 12:00:00'),
    (5, 3, 10.0, 'IN', 'KG', '2024-01-04 09:00:00'),
    (6, 3, 1.0, 'OUT', 'KG', '2024-01-06 13:00:00'),
    (7, 4, 3.0, 'IN', 'KG', '2024-01-05 10:00:00'),
    (8, 4, 0.3, 'OUT', 'KG', '2024-01-06 14:00:00'),
    (9, 5, 2.5, 'IN', 'KG', '2024-01-05 10:00:00'),
    (10, 5, 0.2, 'OUT', 'KG', '2024-01-06 14:00:00');

