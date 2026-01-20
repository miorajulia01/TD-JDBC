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

CREATE  TYPE unit_type AS ENUM (
    'PCS',
    'KG',
    'L'
)

CREATE TABLE Dish
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type dish_type NOT NULL,
    price numeric (10, 2)
);


CREATE TABLE Ingredient
(
    id       int PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    price    NUMERIC(6, 2)       NOT NULL,
    category ingredient_category NOT NULL

);

CREATE TABLE dish_ingredient(
    id serial PRIMARY KEY ,
    quantity_required numeric (6, 2) NOT NULL ,
    id_dish INT NOT NULL REFERENCES Ingredient(id) ON DELETE CASCADE ,
    id_ingredient INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity_required NUMERIC(6,2) NOT NULL,
    unit unit_type NOT NULL
);
