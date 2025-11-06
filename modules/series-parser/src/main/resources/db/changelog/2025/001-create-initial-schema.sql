-- liquibase formatted sql

-- changeset nikoir:001-create-initial-schema
-- comment: Создание основных таблиц

CREATE TABLE "series" (
  "id" integer PRIMARY KEY,
  "title" varchar NOT NULL,
  "original_title" varchar NOT NULL,
  "total_seasons" integer NOT NULL,
  "status" varchar NOT NULL,
  "release_date" date NOT NULL,
  "poster_url" varchar,
  "kinopoisk_id" varchar,
  "imdb_id" varchar
);

CREATE TABLE "season" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "release_date" date NOT NULL,
  "total_episodes" integer NOT NULL,
  "series_id" integer NOT NULL
);

CREATE TABLE "episode" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "release_date" date NOT NULL,
  "season_id" integer NOT NULL,
  "is_processed" boolean NOT NULL DEFAULT false,
  "processed_at" timestamp NOT NULL
);

CREATE TABLE "dub_studio" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "slug" varchar NOT NULL,
  "aliases" varchar[]
);

CREATE TABLE "quality" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "slug" varchar NOT NULL,
  "aliases" varchar[],
  "resolution_width" integer NOT NULL,
  "resolution_height" integer NOT NULL
);

CREATE TABLE "episode_release" (
  "id" integer PRIMARY KEY,
  "episode_id" integer NOT NULL,
  "dub_studio_id" integer,
  "source_id" integer,
  "quality_id" integer,
  "release_timestamp" timestamp NOT NULL
);

CREATE TABLE "source" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "root_url" varchar NOT NULL,
  "url_template" varchar NOT NULL
);

CREATE TABLE "user" (
  "id" integer PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "user_subscription" (
  "id" integer PRIMARY KEY,
  "user_id" integer,
  "series_id" integer,
  "source_id" integer,
  "dub_studio_id" integer,
  "quality_id" integer
);

ALTER TABLE "season" ADD FOREIGN KEY ("series_id") REFERENCES "series" ("id");

ALTER TABLE "episode" ADD FOREIGN KEY ("season_id") REFERENCES "season" ("id");

ALTER TABLE "episode_release" ADD FOREIGN KEY ("episode_id") REFERENCES "episode" ("id");

ALTER TABLE "episode_release" ADD FOREIGN KEY ("quality_id") REFERENCES "quality" ("id");

ALTER TABLE "episode_release" ADD FOREIGN KEY ("dub_studio_id") REFERENCES "dub_studio" ("id");

ALTER TABLE "episode_release" ADD FOREIGN KEY ("source_id") REFERENCES "source" ("id");

ALTER TABLE "user_subscription" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("id");

ALTER TABLE "user_subscription" ADD FOREIGN KEY ("series_id") REFERENCES "series" ("id");

ALTER TABLE "user_subscription" ADD FOREIGN KEY ("source_id") REFERENCES "source" ("id");

ALTER TABLE "user_subscription" ADD FOREIGN KEY ("dub_studio_id") REFERENCES "dub_studio" ("id");

ALTER TABLE "user_subscription" ADD FOREIGN KEY ("quality_id") REFERENCES "quality" ("id");
