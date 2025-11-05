#!/bin/bash

set -Eeo pipefail

source "$(which docker-entrypoint.sh)"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	------------ create roles and grant db-level permissions
	CREATE ROLE $PG_DBO_USER WITH LOGIN PASSWORD '$PG_DBO_PWD';
	CREATE ROLE $PG_API_USER WITH LOGIN PASSWORD '$PG_API_PWD';

	-- only explicitly allowed roles can connect
	REVOKE ALL ON DATABASE $POSTGRES_DB FROM public;

	GRANT connect ON DATABASE $POSTGRES_DB TO $PG_DBO_USER, $PG_API_USER;
	GRANT create ON DATABASE $POSTGRES_DB TO $PG_DBO_USER;


	------------ create schema
	CREATE SCHEMA IF NOT EXISTS $POSTGRES_SCHEMA AUTHORIZATION $PG_DBO_USER;

	------------ schema permissions for api user
	GRANT USAGE ON SCHEMA $POSTGRES_SCHEMA TO $PG_API_USER;

	-- allow DML on future tables created by postgres and dbo users
	ALTER DEFAULT privileges for role postgres, $PG_DBO_USER IN SCHEMA $POSTGRES_SCHEMA
	GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO $PG_API_USER;

	-- allow DML on all existing tables
	GRANT SELECT, INSERT, UPDATE, DELETE on all tables in schema $POSTGRES_SCHEMA TO $PG_API_USER;
	-----------------------------


	------------ schema permissions for dbo
	-- includes usage and create (allows to create objects within schema)
	grant ALL PRIVILEGES ON SCHEMA $POSTGRES_SCHEMA, public TO $PG_DBO_USER;

	-- for future objects created by postgres user
	ALTER DEFAULT privileges for role postgres IN SCHEMA $POSTGRES_SCHEMA, public
	GRANT ALL PRIVILEGES ON TABLES TO $PG_DBO_USER;

	ALTER DEFAULT privileges for role postgres IN SCHEMA $POSTGRES_SCHEMA, public
	GRANT ALL PRIVILEGES ON SEQUENCES TO $PG_DBO_USER;

	-- for existing objects
	GRANT ALL PRIVILEGES on all tables in schema $POSTGRES_SCHEMA, public TO $PG_DBO_USER;
	GRANT ALL privileges on all sequences IN SCHEMA $POSTGRES_SCHEMA, public TO $PG_DBO_USER;
	-----------------------------
	CREATE EXTENSION pg_stat_statements;
EOSQL
