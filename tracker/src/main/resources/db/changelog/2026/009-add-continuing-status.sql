-- liquibase formatted sql

-- changeset nikoir:009-add-continuing-status
-- comment: Добавление нового статуса continuing для сериалов

-- Изменяем CHECK constraint, добавляя новое значение continuing
ALTER TABLE "series" DROP CONSTRAINT chk_status;

ALTER TABLE "series" ADD CONSTRAINT chk_status CHECK (status in ('FILMING',
'PRE-PRODUCTION',
'COMPLETED',
'ANNOUNCED',
'POST-PRODUCTION',
'CONTINUING'));