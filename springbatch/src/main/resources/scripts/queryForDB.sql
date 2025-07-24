use testdb;

CREATE TABLE customer(
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    contact_no VARCHAR(255)
);

CREATE TABLE customer_copy (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    contact_no VARCHAR(255)
);

select * from customer_copy;


select * from customer;

drop procedure if exists insert_customers;

DELIMITER $$



CREATE PROCEDURE insert_customers()
BEGIN
    DECLARE i INT DEFAULT 1;

    WHILE i <= 10000 DO
        INSERT INTO customer (id, name, address, contact_no)
        VALUES (
            i,
            CONCAT('Customer ', i),
            CONCAT('Address ', i),
            CONCAT('99999999', LPAD(i, 2, '0'))
        );
        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;

-- Call the procedure
CALL insert_customers();



