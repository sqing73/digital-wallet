\getenv auth_user AUTH_DB_USER
\getenv auth_pwd AUTH_DB_PASSWORD
\getenv wallet_user WALLET_DB_USER
\getenv wallet_pwd WALLET_DB_PASSWORD
\getenv notification_user NOTIFICATION_DB_USER
\getenv notification_pwd NOTIFICATION_DB_PASSWORD

CREATE USER :"auth_user" WITH PASSWORD :'auth_pwd';
CREATE DATABASE auth_db OWNER :"auth_user";
GRANT ALL PRIVILEGES ON DATABASE auth_db TO :"auth_user";

CREATE USER :"wallet_user" WITH PASSWORD :'wallet_pwd';
CREATE DATABASE wallet_db OWNER :"wallet_user";
GRANT ALL PRIVILEGES ON DATABASE wallet_db TO :"wallet_user";

CREATE USER :"notification_user" WITH PASSWORD :'notification_pwd';
CREATE DATABASE notification_db OWNER :"notification_user";
GRANT ALL PRIVILEGES ON DATABASE notification_db TO :"notification_user";
