-- Création database
CREATE DATABASE mini_dish_db;

-- Création d'utilisateur
CREATE USER mini_dish_db_manager WITH PASSWORD '123456';

-- Pour se connecter
GRANT CONNECT ON DATABASE mini_dish_db to mini_dish_db_manager;

-- Connexion database
\c mini_dish_db;

-- Privilège
-- Création table
GRANT CREATE ON schema public TO mini_dish_db_manager;

-- Crud
ALTER DEFAULT PRIVILEGES IN schema public
      grant SELECT, INSERT , UPDATE , DELETE ON TABLES TO mini_dish_db_manager;

-- Autoriser l'usage des séquences (pour SERIAL/auto-increment)
ALTER DEFAULT PRIVILEGES IN SCHEMA public
    GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO product_manager_user;