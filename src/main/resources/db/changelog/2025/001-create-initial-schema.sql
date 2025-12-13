-- liquibase formatted sql

-- changeset nikoir:001-create-initial-schema
-- comment: Создание основных таблиц

CREATE TABLE "series" (
  "id" BIGSERIAL PRIMARY KEY,
  "title" varchar NOT NULL,
  "eng_title" varchar,
  "total_seasons" integer,
  "status" varchar,
  "release_year" integer NOT NULL,
  "poster_url" varchar,
  "external_ids" JSONB DEFAULT '{}',
  CONSTRAINT chk_status CHECK (status in ('filming', 'pre-production', 'completed', 'announced', 'post-production'))
);

CREATE TABLE "season" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar,
  "number" integer NOT NULL,
  "release_date" date,
  "total_episodes" integer,
  "series_id" integer NOT NULL
);

CREATE TABLE "episode" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar,
  "number" integer NOT NULL,
  "release_date" date,
  "season_id" integer NOT NULL
);

CREATE TABLE "dub_studio" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar NOT NULL,
  "slug" varchar NOT NULL,
  "aliases" varchar[]
);

CREATE TABLE "quality" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar NOT NULL,
  "slug" varchar NOT NULL,
  "aliases" varchar[],
  "resolution_width" integer NOT NULL,
  "resolution_height" integer NOT NULL
);

CREATE TABLE "episode_release" (
  "id" BIGSERIAL PRIMARY KEY,
  "episode_id" integer NOT NULL,
  "dub_studio_id" integer,
  "source_id" integer,
  "quality_id" integer,
  "release_timestamp" timestamp NOT NULL
);

CREATE TABLE "source" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar NOT NULL,
  "root_url" varchar NOT NULL,
  "url_template" varchar NOT NULL
);

CREATE TABLE "user" (
  "id" BIGSERIAL PRIMARY KEY,
  "name" varchar NOT NULL
);

CREATE TABLE "user_subscription" (
  "id" BIGSERIAL PRIMARY KEY,
  "user_id" integer NOT NULL,
  "series_id" integer NOT NULL,
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
