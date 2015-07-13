# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

CREATE TABLE "POSTTAGS" (
  "id"    VARCHAR NOT NULL PRIMARY KEY,
  "title" VARCHAR NOT NULL
);
CREATE TABLE "POST_TAGS" (
  "postId" VARCHAR NOT NULL,
  "tagId"  VARCHAR NOT NULL
);
CREATE TABLE "Profiles" (
  "ID"      INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
  "USER_ID" INTEGER NOT NULL,
  "ABOUT"   VARCHAR NOT NULL,
  "AVATAR"  VARCHAR NOT NULL
);
CREATE TABLE "posts" (
  "ID"           VARCHAR   NOT NULL PRIMARY KEY,
  "ITEM_TITLE"   VARCHAR   NOT NULL,
  "TYPE"         INTEGER   NOT NULL,
  "DATE_CREATED" TIMESTAMP NOT NULL,
  "AUTHOR"       VARCHAR   NOT NULL,
  "CONTENT"      VARCHAR   NOT NULL,
  "EXTRA_DATA"   VARCHAR   NOT NULL,
  "DRAFT"        BOOLEAN   NOT NULL,
  "USER_ID"      INTEGER
);
CREATE TABLE "user_alias" (
  "ID"     VARCHAR NOT NULL PRIMARY KEY,
  "USERID" INTEGER NOT NULL,
  "ALIAS"  VARCHAR NOT NULL,
  "about"  VARCHAR
);
CREATE TABLE "users" (
  "ID"          INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY,
  "EMAIL"       VARCHAR NOT NULL,
  "PASSWORD"    VARCHAR NOT NULL,
  "NAME"        VARCHAR NOT NULL,
  "ROLE"        VARCHAR NOT NULL,
  "ALIAS_LIMIT" INTEGER
);
ALTER TABLE "POST_TAGS" ADD CONSTRAINT "post_fk" FOREIGN KEY ("postId") REFERENCES "posts" ("ID") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "POST_TAGS" ADD CONSTRAINT "tag_fk" FOREIGN KEY ("tagId") REFERENCES "POSTTAGS" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "Profiles" ADD CONSTRAINT "USER_FK" FOREIGN KEY ("USER_ID") REFERENCES "users" ("ID") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "user_alias" ADD CONSTRAINT "ACCOUNT_FK" FOREIGN KEY ("USERID") REFERENCES "users" ("ID") ON UPDATE NO ACTION ON DELETE NO ACTION;

# --- !Downs

ALTER TABLE "user_alias" DROP CONSTRAINT "ACCOUNT_FK";
ALTER TABLE "Profiles" DROP CONSTRAINT "USER_FK";
ALTER TABLE "POST_TAGS" DROP CONSTRAINT "post_fk";
ALTER TABLE "POST_TAGS" DROP CONSTRAINT "tag_fk";
DROP TABLE "users";
DROP TABLE "user_alias";
DROP TABLE "posts";
DROP TABLE "Profiles";
DROP TABLE "POST_TAGS";
DROP TABLE "POSTTAGS";

