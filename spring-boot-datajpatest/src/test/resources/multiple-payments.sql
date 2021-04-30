INSERT INTO orders (id, date, amount, paid) VALUES (1, NOW(), 100.0, true);
INSERT INTO orders (id, date, amount, paid) VALUES (2, NOW() - INTERVAL '1 DAY', 50.0, true);
INSERT INTO orders (id, date, amount, paid) VALUES (3, NOW(), 70.0, false);

INSERT INTO payment (id, order_id, credit_card_number) VALUES (1, 1, '4532756279624064');
INSERT INTO payment (id, order_id, credit_card_number) VALUES (2, 2, '4716327217780406');
