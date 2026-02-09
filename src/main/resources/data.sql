-- Default categories (will be inserted after schema creation)
-- These categories are system-wide and cannot be deleted or modified
INSERT INTO category (id, name, type, is_default, user_id) VALUES 
(1, 'Salary', 'INCOME', true, NULL),
(2, 'Freelance', 'INCOME', true, NULL),
(3, 'Investments', 'INCOME', true, NULL),
(4, 'Food', 'EXPENSE', true, NULL),
(5, 'Rent', 'EXPENSE', true, NULL),
(6, 'Transportation', 'EXPENSE', true, NULL),
(7, 'Entertainment', 'EXPENSE', true, NULL),
(8, 'Healthcare', 'EXPENSE', true, NULL),
(9, 'Utilities', 'EXPENSE', true, NULL);
