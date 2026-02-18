-- liquibase formatted sql

-- changeset nikoir:008-alter-series-table
-- comment: Изменение структуры таблицы series
ALTER TABLE "series" ADD COLUMN "description" TEXT;

ALTER TABLE "series" ADD COLUMN "countries" VARCHAR(255)[];
