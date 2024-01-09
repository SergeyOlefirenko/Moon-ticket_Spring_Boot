create database if not exists moonticketDB default char set utf8;
use moonticketDB;
grant all privileges on moonticketDB.* to root identified by 'root';
SHOW VARIABLES LIKE 'character_set_database';