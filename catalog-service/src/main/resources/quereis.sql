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
 4. items
    CREATE TABLE items(
    id UUID PRIMARY KEY,
    name text NOT NULL,
    brand_id UUID REFERENCES brands (id) ON DELETE CASCADE,
    quantity smallint
    CONSTRAINT check_quantity_not_below_one CHECK (quantity >= 1)
    );
 5. brands_categories - Many To Many Relationship [each brand can fall into many categories and each category can be for multiple brands]
    CREATE TABLE brands_categories(
    brand_id UUID REFERENCES brands (id) ON DELETE CASCADE,
    category_id UUID REFERENCES categories (id) ON DELETE CASCADE
    );
