-- liquibase formatted sql

-- changeset nikoir:014-update-url-templates.sql
-- comment: Обновление столбцов url_template

UPDATE "source"
SET url_template = 'https://movielab.one/movies/{kinopoisk_id}'
WHERE id = 1;

UPDATE "source"
SET url_template = 'https://www.wikidata.org/wiki/{wikidata_id}'
WHERE id = 2;
