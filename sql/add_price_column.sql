DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'dish'
              AND column_name = 'price'
        ) THEN
            ALTER TABLE Dish ADD COLUMN price NUMERIC(10, 2);
        END IF;
    END $$;

UPDATE Dish SET price = 2000.00 WHERE id = 1;
UPDATE Dish SET price = 6000.00 WHERE id = 2;