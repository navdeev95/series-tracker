-- liquibase formatted sql

-- changeset nikoir:005-drop-slug-columns
-- comment: Удаление колонок slug из таблиц dub_studio и quality
ALTER TABLE "dub_studio"
DROP COLUMN slug;

ALTER TABLE "quality"
DROP COLUMN slug;
