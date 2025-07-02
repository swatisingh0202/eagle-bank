CREATE TABLE users (
                       user_id UUID PRIMARY KEY,
                       name VARCHAR(100),
                       email VARCHAR(100),
                       phone VARCHAR(20),
                       created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE identity (
                          identity_id UUID PRIMARY KEY,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          user_id UUID NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE bank_account (
                              account_id UUID PRIMARY KEY,
                              name VARCHAR(100),
                              account_type VARCHAR(20),
                              account_number VARCHAR(20) UNIQUE NOT NULL,
                              sort_code VARCHAR(20),
                              balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                              currency VARCHAR(10),
                              user_id UUID NOT NULL,
                              created_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);


CREATE TABLE transaction (
                             transaction_id UUID PRIMARY KEY,
                             amount DOUBLE PRECISION NOT NULL,
                             type VARCHAR(10) NOT NULL,
                             timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             account_id UUID NOT NULL,
                             FOREIGN KEY (account_id) REFERENCES bank_account(account_id) ON DELETE CASCADE
);
