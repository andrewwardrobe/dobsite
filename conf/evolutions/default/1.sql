# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "POSTTAGS" ("id" VARCHAR NOT NULL PRIMARY KEY,"title" VARCHAR NOT NULL);
create table "POST_TAGS" ("postId" VARCHAR NOT NULL,"tagId" VARCHAR NOT NULL);
create table "discography" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"NAME" VARCHAR NOT NULL,"RELEASE_TYPE" INTEGER NOT NULL,"IMAGE" VARCHAR NOT NULL);
create table "posts" ("ID" VARCHAR NOT NULL PRIMARY KEY,"ITEM_TITLE" VARCHAR NOT NULL,"TYPE" INTEGER NOT NULL,"DATE_CREATED" TIMESTAMP NOT NULL,"AUTHOR" VARCHAR NOT NULL,"CONTENT" VARCHAR NOT NULL,"EXTRA_DATA" VARCHAR NOT NULL,"DRAFT" BOOLEAN NOT NULL);
create table "users" ("ID" INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,"EMAIL" VARCHAR NOT NULL,"PASSWORD" VARCHAR NOT NULL,"NAME" VARCHAR NOT NULL,"ROLE" VARCHAR NOT NULL);
alter table "POST_TAGS" add constraint "post_fk" foreign key("postId") references "posts"("ID") on update NO ACTION on delete NO ACTION;
alter table "POST_TAGS" add constraint "tag_fk" foreign key("tagId") references "POSTTAGS"("id") on update NO ACTION on delete NO ACTION;

# --- !Downs

alter table "POST_TAGS" drop constraint "post_fk";
alter table "POST_TAGS" drop constraint "tag_fk";
drop table "users";
drop table "posts";
drop table "discography";
drop table "POST_TAGS";
drop table "POSTTAGS";

