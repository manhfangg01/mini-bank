drop database if exists mini_bank;
create database mini_bank;
use  mini_bank;

create table users(
                      id int primary key auto_increment,
                      username nvarchar(50) unique not null,
                      password_hash nvarchar(255) not null, -- add salt
                      full_name nvarchar(50),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table accounts(
                         id int primary key auto_increment,
                         user_id int unique not null, -- user(1) - account(1)
                         account_number char(20) not null,
                         balance decimal(15,2) default 0.00,
                         status varchar(20) default 'ACTIVE', -- ACTIVE, LOCKED, CLOSED
                         daily_transfer_limit DECIMAL(15, 2) DEFAULT 50000000,
                         daily_transfer_used DECIMAL(15, 2) DEFAULT 0.00,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                              id VARCHAR(50) PRIMARY KEY, -- UUID
                              sender_account_id INT, -- NULL if depositing
                              receiver_account_id INT, -- NULL if withdrawing
                              amount DECIMAL(15, 2) NOT NULL,
                              fee DECIMAL(15, 2) DEFAULT 0.00,
                              type VARCHAR(20) NOT NULL, -- TRANSFER, DEPOSIT, WITHDRAW
                              status VARCHAR(20) NOT NULL, -- SUCCESS, FAILED
                              message VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

alter table accounts
    add constraint FK_Account_User
        foreign key (user_id)
            references users (id);

alter table transactions
    add constraint FK_Transactions_Sender
        foreign key (sender_account_id)
            references accounts (id);

alter table transactions
    add constraint FK_Transactions_Receiver
        foreign key (receiver_account_id)
            references users (id);