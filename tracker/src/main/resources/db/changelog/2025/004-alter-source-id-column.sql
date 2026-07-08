-- liquibase formatted sql

-- changeset nikoir:004-alter-source-id-column
-- comment: Изменение колонки source_id в таблице episode_release
ALTER TABLE "episode_release"
ALTER COLUMN "source_id" SET NOT NULL;