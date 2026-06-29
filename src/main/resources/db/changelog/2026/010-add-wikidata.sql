-- liquibase formatted sql

-- changeset nikoir:010-add-wikidata
-- comment: Добавление wikidata

INSERT INTO "external_id"("name") VALUES('wikidata');

INSERT INTO "source" ("name", "root_url", "url_template")
VALUES ('WikiData', 'https://www.wikidata.org/wiki/', 'https://www.wikidata.org/wiki/%s');