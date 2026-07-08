-- liquibase formatted sql

-- changeset nikoir:013-drop-total-episodes-column.sql
-- comment: Удаление колонки total_episodes из таблицы series

ALTER TABLE "series"
DROP COLUMN "total_episodes";
