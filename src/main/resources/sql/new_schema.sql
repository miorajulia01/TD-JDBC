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
);

CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TABLE dish
(
    id serial PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dish_type dish_type NOT NULL,
    price numeric (10, 2)
);


CREATE TABLE ingredient
(
    id       serial PRIMARY KEY,
    name     VARCHAR(100)        NOT NULL,
    price    NUMERIC(6, 2)       NOT NULL,
    category ingredient_category NOT NULL

);

CREATE TABLE dish_ingredient(
    id serial PRIMARY KEY ,
    quantity_required numeric (6, 2) NOT NULL ,
    id_dish INT NOT NULL REFERENCES dish(id) ON DELETE CASCADE ,
    id_ingredient INT NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    unit unit_type NOT NULL
);

CREATE TABLE stock_movement (
    id SERIAL PRIMARY KEY,
    id_ingredient INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity NUMERIC(10, 2) NOT NULL,
    type movement_type NOT NULL,
    unit VARCHAR(10) NOT NULL DEFAULT 'KG',
    creation_datetime TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
