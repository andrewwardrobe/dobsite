# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "biography" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL,"BIO_TYPE" INTEGER NOT NULL,"IMAGE_PATH" VARCHAR NOT NULL,"THUMB_PATH" VARCHAR NOT NULL,"BIO_TEXT" VARCHAR NOT NULL);
create table "discography" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL,"RELEASE_TYPE" INTEGER NOT NULL,"IMAGE" VARCHAR NOT NULL);
create table "person" ("name" VARCHAR NOT NULL PRIMARY KEY,"age" VARCHAR NOT NULL);
create table "posts" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"ITEM_TITLE" VARCHAR NOT NULL,"TYPE" INTEGER NOT NULL,"DATE_CREATED" TIMESTAMP NOT NULL,"AUTHOR" VARCHAR NOT NULL,"CONTENT" VARCHAR NOT NULL,"EXTRA_DATA" VARCHAR NOT NULL);
create table "tracks" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"RELEASE_ID" INTEGER NOT NULL,"POSITION" INTEGER NOT NULL,"NAME" VARCHAR NOT NULL);
create table "users" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"EMAIL" VARCHAR NOT NULL,"PASSWORD" VARCHAR NOT NULL,"NAME" VARCHAR NOT NULL,"ROLE" VARCHAR NOT NULL);
alter table "tracks" add constraint "REL_FK" foreign key("RELEASE_ID") references "discography"("ID") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "tracks" drop constraint "REL_FK";
drop table "users";
drop table "tracks";
drop table "posts";
drop table "person";
drop table "discography";
drop table "biography";

