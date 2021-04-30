CREATE SEQUENCE hibernate_sequence
    START 1 INCREMENT 1;

CREATE TABLE orders(
    id INT NOT NULL PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    amount NUMERIC NOT NULL,
    paid BOOLEAN NOT NULL
);

CREATE TABLE payment(
    id INT NOT NULL PRIMARY KEY,
    order_id INT NOT NULL UNIQUE,
    credit_card_number VARCHAR(16) NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(id)
);
