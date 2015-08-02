# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `POSTTAGS` (`id` VARCHAR(254) NOT NULL PRIMARY KEY,`title` VARCHAR(254) NOT NULL);
create table `POST_TAGS` (`postId` VARCHAR(254) NOT NULL,`tagId` VARCHAR(254) NOT NULL);
create table `posts` (`ID` VARCHAR(254) NOT NULL PRIMARY KEY,`ITEM_TITLE` VARCHAR(254) NOT NULL,`TYPE` INTEGER NOT NULL,`DATE_CREATED` TIMESTAMP NOT NULL,`AUTHOR` VARCHAR(254) NOT NULL,`CONTENT` TEXT NOT NULL,`EXTRA_DATA` VARCHAR(254) NOT NULL,`DRAFT` BOOLEAN NOT NULL,`USER_ID` INTEGER);
alter table `POST_TAGS` add constraint `post_fk` foreign key(`postId`) references `posts`(`ID`) on update NO ACTION on delete NO ACTION;
alter table `POST_TAGS` add constraint `tag_fk` foreign key(`tagId`) references `POSTTAGS`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE POST_TAGS DROP FOREIGN KEY post_fk;
ALTER TABLE POST_TAGS DROP FOREIGN KEY tag_fk;
drop table `posts`;
drop table `POST_TAGS`;
drop table `POSTTAGS`;

