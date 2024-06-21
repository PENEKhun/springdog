-- books_schema_and_data.sql
-- Drop existing table if exists, create new schema and insert test data

-- Step 1: Drop the Book table if it exists
DROP TABLE IF EXISTS Book;

-- Step 2: Create the Book table schema
CREATE TABLE Book (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      description TEXT,
                      price BIGINT NOT NULL
);
