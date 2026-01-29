create DATABASE mini_dish_db;

\c mini_dish_db;

CREATE USER mini_dish_db_manager WITH PASSWORD '123456';


GRANT USAGE ON SCHEMA public TO mini_dish_db_manager;
GRANT CREATE ON SCHEMA public TO mini_dish_db_manager;


GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO mini_dish_db_manager;


ALTER TABLE dish OWNER TO mini_dish_db_manager;
ALTER TABLE ingredient OWNER TO mini_dish_db_manager;
ALTER TABLE dish_ingredient OWNER TO mini_dish_db_manager;
ALTER TABLE stock_movement  OWNER TO mini_dish_db_manager;
ALTER TABLE "order"  OWNER TO mini_dish_db_manager;
ALTER TABLE dish_order  OWNER TO mini_dish_db_manager;
ALTER SEQUENCE dish_id_seq OWNER TO mini_dish_db_manager;
--évaluation
ALTER TABLE "table"  OWNER TO mini_dish_db_manager;
ALTER TABLE table_order  OWNER TO mini_dish_db_manager;