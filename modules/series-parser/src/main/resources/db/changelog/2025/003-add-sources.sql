-- liquibase formatted sql

-- changeset nikoir:003-add-sources
-- comment: Добавление внешнего источника

ALTER TABLE "source"
ADD CONSTRAINT UNIQUE_NAME UNIQUE("name");

INSERT INTO "source" ("name", "root_url", "url_template")
VALUES ('MovieLab', 'https://movielab.one/', 'https://movielab.one/movies/%s');