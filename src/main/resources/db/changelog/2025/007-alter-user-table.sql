-- liquibase formatted sql

-- changeset nikoir:007-alter-user-table
-- comment: Изменение структуры таблицы user
ALTER TABLE "user" DROP COLUMN "name";

ALTER TABLE "user" ADD COLUMN "telegram_id" BIGINT NOT NULL;

ALTER TABLE "user" ADD CONSTRAINT UNIQUE_TELEGRAM_ID UNIQUE("telegram_id");
