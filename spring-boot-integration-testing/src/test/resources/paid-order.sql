INSERT INTO orders (id, date, amount, paid) VALUES (1, current_date, 100.0, true);
INSERT INTO payment (id, order_id, credit_card_number) VALUES (1, 1, '4532756279624064');
