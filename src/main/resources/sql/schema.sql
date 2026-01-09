CREATE TYPE ingredient_category AS ENUM (
    'VEGETABLE',
    'ANIMAL',
    'MARINE',
    'DAIRY',
    'OTHER'
);

CREATE TYPE dish_type AS ENUM (
    'START',
    'MAIN',
    'DESSERT'
);

CREATE TABLE Dish
(
    id int PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type dish_type NOT NULL
);

ALTER TABLE dish ADD COLUMN IF NOT EXISTS price NUMERIC(10, 2);
UPDATE dish SET price = 2000.00 WHERE name = 'Salade fraîche';
UPDATE dish SET price = 6000.00 WHERE name = 'Poulet grillé';


CREATE TABLE Ingredient
(
    id       int PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    price    NUMERIC(6, 2)       NOT NULL,
    category ingredient_category NOT NULL,
    id_dish  INT,
    CONSTRAINT fk_dish
    FOREIGN KEY (id_dish)
    REFERENCES Dish (id)
);

