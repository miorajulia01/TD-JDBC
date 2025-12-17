create DATABASE mini_dish_db;

\c mini_dish_db;

CREATE USER mini_dish_db_manager WITH PASSWORD '123456';

GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mini_dish_db_manager;