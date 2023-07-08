DB Schema:

Database:
    CREATE DATABASE catalog_service;

Entities:
 1. user_type - enum
    CREATE TYPE user_type AS ENUM ('admin', 'simple_user');
 1. users
    CREATE TABLE users(
    id UUID PRIMARY KEY,
    first_name text NOT NULL,
    last_name text NOT NULL,
    user_type user_type NOT NULL,
    email text UNIQUE NOT NULL
    );
    #Create hash index on email for efficient searching
    CREATE INDEX email_hash_index ON users USING HASH (email);
 2. categories
    CREATE TABLE categories(
    id UUID PRIMARY KEY,
    name text NOT NULL UNIQUE
    );
 3. brands
    CREATE TABLE brands(
    id UUID PRIMARY KEY,
    name text NOT NULL UNIQUE
    );
 6. Shop Commands
     CREATE TABLE shop_meta_data(
     id UUID PRIMARY Key,
     name text NOT NULL UNIQUE,
     owner_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE
     );
     CREATE INDEX name_hash_index ON shop_meta_data USING HASH (name);
 4. items
    CREATE TABLE item_meta_data(
    id UUID PRIMARY KEY,
    name text NOT NULL,
    brand_id UUID REFERENCES brands (id) ON DELETE CASCADE,
    quantity smallint NOT NULL,
    shop_id UUID REFERENCES shop_meta_data (id) ON DELETE CASCADE
    CONSTRAINT check_quantity_not_below_one CHECK (quantity >= 1)
    );
    #ITEM AND SHOPS have MANY TO MANY RELATIONSHIP
    CREATE TABLE items_shops(
       item_id UUID REFERENCES item_meta_data (id) ON DELETE CASCADE,
       shop_id UUID REFERENCES shop_meta_data (id) ON DELETE CASCADE
    );
    #ITEM AND Category have MANY TO MANY RELATIONSHIP [EACH ITEM CAN BE TAGGED WITH 5 Categories]
    CREATE TABLE item_category(
       item_id UUID REFERENCES item_meta_data (id) ON DELETE CASCADE,
       category_id UUID REFERENCES categories (id) ON DELETE CASCADE
    );

