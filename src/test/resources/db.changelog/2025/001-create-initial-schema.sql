-- liquibase formatted sql

-- changeset nikoir:final-schema
-- comment: Final database schema for H2 database

-- Create series table
CREATE TABLE "series" (
  "id" BIGSERIAL PRIMARY KEY,
  "title" VARCHAR NOT NULL,
  "eng_title" VARCHAR,
  "total_seasons" INTEGER,
  "status" VARCHAR,
  "release_year" INTEGER NOT NULL,
  "poster_url" VARCHAR,
  "description" TEXT,
  "countries" VARCHAR(255) ARRAY,
  CONSTRAINT "chk_status" CHECK ("status" IN (
    'FILMING',
    'PRE_PRODUCTION',
    'COMPLETED',
    'ANNOUNCED',
    'POST_PRODUCTION',
    'DELETED'
  ))
);

-- Create season table
CREATE TABLE "season" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR,
  "number" INTEGER NOT NULL,
  "release_date" DATE,
  "total_episodes" INTEGER,
  "series_id" INTEGER NOT NULL,
  FOREIGN KEY ("series_id") REFERENCES "series" ("id")
);

-- Create episode table
CREATE TABLE "episode" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR,
  "number" INTEGER NOT NULL,
  "release_date" DATE,
  "season_id" INTEGER NOT NULL,
  FOREIGN KEY ("season_id") REFERENCES "season" ("id")
);

-- Create dub_studio table
CREATE TABLE "dub_studio" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "aliases" VARCHAR ARRAY
);

-- Create quality table
CREATE TABLE "quality" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "aliases" VARCHAR ARRAY,
  "resolution_width" INTEGER NOT NULL,
  "resolution_height" INTEGER NOT NULL
);

-- Create source table
CREATE TABLE "source" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "root_url" VARCHAR NOT NULL,
  "url_template" VARCHAR NOT NULL,
  CONSTRAINT UNIQUE_NAME UNIQUE("name")
);

-- Create episode_release table
CREATE TABLE "episode_release" (
  "id" BIGSERIAL PRIMARY KEY,
  "episode_id" INTEGER NOT NULL,
  "dub_studio_id" INTEGER,
  "source_id" INTEGER NOT NULL,
  "quality_id" INTEGER,
  "release_timestamp" TIMESTAMP NOT NULL,
  FOREIGN KEY ("episode_id") REFERENCES "episode" ("id"),
  FOREIGN KEY ("quality_id") REFERENCES "quality" ("id"),
  FOREIGN KEY ("dub_studio_id") REFERENCES "dub_studio" ("id"),
  FOREIGN KEY ("source_id") REFERENCES "source" ("id")
);

-- Create user table
CREATE TABLE "user" (
  "id" BIGSERIAL PRIMARY KEY,
  "telegram_id" BIGINT NOT NULL,
  CONSTRAINT UNIQUE_TELEGRAM_ID UNIQUE("telegram_id")
);

-- Create external_id table
CREATE TABLE "external_id" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" VARCHAR NOT NULL
);

-- Create external_id_series table
CREATE TABLE "external_id_series" (
  "id" BIGSERIAL PRIMARY KEY,
  "series_id" INTEGER NOT NULL,
  "external_id" INTEGER NOT NULL,
  "value" VARCHAR NOT NULL,
  FOREIGN KEY ("series_id") REFERENCES "series" ("id"),
  FOREIGN KEY ("external_id") REFERENCES "external_id" ("id"),
  CONSTRAINT unique_series_external_id UNIQUE("series_id", "external_id")
);

-- Create user_subscription table
CREATE TABLE "user_subscription" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" INTEGER NOT NULL,
  "series_id" INTEGER NOT NULL,
  "source_id" INTEGER,
  "dub_studio_id" INTEGER,
  "quality_id" INTEGER,
  FOREIGN KEY ("user_id") REFERENCES "user" ("id"),
  FOREIGN KEY ("series_id") REFERENCES "series" ("id"),
  FOREIGN KEY ("source_id") REFERENCES "source" ("id"),
  FOREIGN KEY ("dub_studio_id") REFERENCES "dub_studio" ("id"),
  FOREIGN KEY ("quality_id") REFERENCES "quality" ("id")
);

-- changeset nikoir:final-data
-- comment: Insert initial data

-- Insert external_id data
INSERT INTO "external_id"("name") VALUES('kinopoisk');
INSERT INTO "external_id"("name") VALUES('IMDB');
INSERT INTO "external_id"("name") VALUES('TMDB');
INSERT INTO "external_id"("name") VALUES('movielab');
INSERT INTO "external_id"("name") VALUES('kinopoisk_hd');

-- Insert source data
INSERT INTO "source" ("name", "root_url", "url_template")
VALUES ('MovieLab', 'https://movielab.one/', 'https://movielab.one/movies/%s');