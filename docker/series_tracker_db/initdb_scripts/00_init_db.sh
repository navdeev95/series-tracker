#!/bin/bash
set -e

echo "🚀 Начинаем инициализацию базы данных..."

# Ждем готовности PostgreSQL
until pg_isready -U "$POSTGRES_USER"; do
    sleep 1
done

echo "✅ PostgreSQL готов"

# Основная инициализация
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Создаем роли
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$PG_DBO_USER') THEN
            CREATE ROLE "$PG_DBO_USER" WITH LOGIN PASSWORD '$PG_DBO_PWD';
        END IF;

        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '$PG_API_USER') THEN
            CREATE ROLE "$PG_API_USER" WITH LOGIN PASSWORD '$PG_API_PWD';
        END IF;
    END
    \$\$;

    -- База данных
    REVOKE ALL ON DATABASE "$POSTGRES_DB" FROM public;
    GRANT CONNECT ON DATABASE "$POSTGRES_DB" TO "$PG_DBO_USER", "$PG_API_USER";
    GRANT CREATE ON DATABASE "$POSTGRES_DB" TO "$PG_DBO_USER";

    -- Схема
    CREATE SCHEMA IF NOT EXISTS "$POSTGRES_SCHEMA";
    ALTER SCHEMA "$POSTGRES_SCHEMA" OWNER TO "$PG_DBO_USER";

    -- Права на схему
    GRANT USAGE ON SCHEMA "$POSTGRES_SCHEMA" TO "$PG_API_USER";

    ALTER DEFAULT PRIVILEGES FOR ROLE POSTGRES, $PG_DBO_USER IN SCHEMA $POSTGRES_SCHEMA
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO $PG_API_USER;

    ALTER DEFAULT PRIVILEGES FOR ROLE POSTGRES, $PG_DBO_USER IN SCHEMA "$POSTGRES_SCHEMA"
    GRANT USAGE, SELECT ON SEQUENCES TO "$PG_API_USER";

    -- Настройки
    ALTER DATABASE "$POSTGRES_DB" SET search_path TO "$POSTGRES_SCHEMA", public;

    -- Расширения
    CREATE EXTENSION IF NOT EXISTS pg_stat_statements;
EOSQL

echo "✅ База данных инициализирована"