INSERT INTO membership_policy (grade, min_amount, max_amount, reward_rate, created_at, updated_at)
VALUES ('NORMAL', 0, 50000, 0.01, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO membership_policy (grade, min_amount, max_amount, reward_rate, created_at, updated_at)
VALUES ('VIP', 50001, 150000, 0.05, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO membership_policy (grade, min_amount, max_amount, reward_rate, created_at, updated_at)
VALUES ('VVIP', 150001, 999999999, 0.1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
