-- liquibase formatted sql

-- changeset nikoir:002-alter-status-constraint
-- comment: Изменение логики проверки constraint-а

-- 1. Сначала удаляем констрейнт
ALTER TABLE series DROP CONSTRAINT IF EXISTS chk_status;

-- 2. Обновляем существующие данные: приводим к uppercase и заменяем дефисы на подчеркивания
UPDATE series
SET status = UPPER(REPLACE(status, '-', '_'))
WHERE status IS NOT NULL;

-- 3. Добавляем новый констрейнт с значениями в UPPERCASE
ALTER TABLE series
ADD CONSTRAINT chk_status
CHECK (status IN (
    'FILMING',
    'PRE_PRODUCTION',
    'COMPLETED',
    'ANNOUNCED',
    'POST_PRODUCTION'
));