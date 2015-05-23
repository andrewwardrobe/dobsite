# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `POSTTAGS` (`id` VARCHAR(254) NOT NULL PRIMARY KEY,`title` VARCHAR(254) NOT NULL);
create table `POST_TAGS` (`postId` VARCHAR(254) NOT NULL,`tagId` VARCHAR(254) NOT NULL);
create table `Profiles` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`USER_ID` INTEGER NOT NULL,`ABOUT` VARCHAR(254) NOT NULL,`AVATAR` VARCHAR(254) NOT NULL);
create table `posts` (`ID` VARCHAR(254) NOT NULL PRIMARY KEY,`ITEM_TITLE` VARCHAR(254) NOT NULL,`TYPE` INTEGER NOT NULL,`DATE_CREATED` TIMESTAMP NOT NULL,`AUTHOR` VARCHAR(254) NOT NULL,`CONTENT` VARCHAR(254) NOT NULL,`EXTRA_DATA` VARCHAR(254) NOT NULL,`DRAFT` BOOLEAN NOT NULL);
create table `users` (`ID` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`EMAIL` VARCHAR(254) NOT NULL,`PASSWORD` VARCHAR(254) NOT NULL,`NAME` VARCHAR(254) NOT NULL,`ROLE` VARCHAR(254) NOT NULL);
alter table `POST_TAGS` add constraint `post_fk` foreign key(`postId`) references `posts`(`ID`) on update NO ACTION on delete NO ACTION;
alter table `POST_TAGS` add constraint `tag_fk` foreign key(`tagId`) references `POSTTAGS`(`id`) on update NO ACTION on delete NO ACTION;
alter table `Profiles` add constraint `USER_FK` foreign key(`USER_ID`) references `users`(`ID`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE Profiles DROP FOREIGN KEY USER_FK;
ALTER TABLE POST_TAGS DROP FOREIGN KEY post_fk;
ALTER TABLE POST_TAGS DROP FOREIGN KEY tag_fk;
drop table `users`;
drop table `posts`;
drop table `Profiles`;
drop table `POST_TAGS`;
drop table `POSTTAGS`;

