-- liquibase formatted sql

-- changeset nikoir:006-add-external-id-table
-- comment: Создание внешней таблицы для развязки external_id - series

CREATE TABLE "external_id" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" varchar NOT NULL
);

ALTER TABLE "series"
DROP COLUMN "external_ids";

CREATE TABLE "external_id_series"(
    "id" BIGSERIAL PRIMARY KEY,
    "series_id" INTEGER NOT NULL,
    "external_id" INTEGER NOT NULL,
    "value" varchar NOT NULL
);

ALTER TABLE "external_id_series" ADD FOREIGN KEY ("series_id") REFERENCES "series" ("id");
ALTER TABLE "external_id_series" ADD FOREIGN KEY ("external_id") REFERENCES "external_id" ("id");
ALTER TABLE "external_id_series" ADD CONSTRAINT unique_series_external_id UNIQUE("series_id", "external_id");

INSERT INTO "external_id"("name") VALUES('kinopoisk');
INSERT INTO "external_id"("name") VALUES('IMDB');
INSERT INTO "external_id"("name") VALUES('TMDB');
INSERT INTO "external_id"("name") VALUES('movielab');
INSERT INTO "external_id"("name") VALUES('kinopoisk_hd');
