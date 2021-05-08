INSERT INTO orders (id, date, amount, paid) VALUES (1, current_date, 100.0, true);
INSERT INTO orders (id, date, amount, paid) VALUES (2, current_date - 1, 50.0, true);

INSERT INTO payment (id, order_id, credit_card_number) VALUES (1, 1, '4532756279624064');
INSERT INTO payment (id, order_id, credit_card_number) VALUES (2, 2, '4716327217780406');
