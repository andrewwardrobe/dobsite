create database daoostinboyeez;
CREATE USER 'testuser'@'localhost' IDENTIFIED BY 'password';
CREATE USER 'testuser'@'localhost.lan' IDENTIFIED BY 'password';
CREATE USER 'testuser'@'leek-server.lan' IDENTIFIED BY 'password';
CREATE USER 'testuser'@'leek-server' IDENTIFIED BY 'password';
CREATE USER 'testuser'@'192.168.0.100' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON daoostinboyeez . * TO 'testuser'@'localhost';
GRANT ALL PRIVILEGES ON daoostinboyeez . * TO 'testuser'@'localhost.lan';
GRANT ALL PRIVILEGES ON daoostinboyeez . * TO 'testuser'@'leek-server';
GRANT ALL PRIVILEGES ON daoostinboyeez . * TO 'testuser'@'leek-server.lan';
GRANT ALL PRIVILEGES ON daoostinboyeez . * TO 'testuser'@'192.168.0.100';