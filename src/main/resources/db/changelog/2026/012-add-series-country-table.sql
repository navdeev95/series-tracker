-- liquibase formatted sql

-- changeset nikoir:012-add-series-country-table
-- comment: Добавление таблицы-развязки series-country
CREATE TABLE "series_country" (
    "id" BIGSERIAL PRIMARY KEY NOT NULL,
    "series_id" integer NOT NULL,
    "country_id" integer NOT NULL
);

ALTER TABLE "series_country" ADD FOREIGN KEY ("series_id") REFERENCES "series" ("id");
ALTER TABLE "series_country" ADD FOREIGN KEY ("country_id") REFERENCES "country" ("id");

INSERT INTO "series_country" ("series_id", "country_id")
select s.id, c.id
from series s
left join
country c on ((c.eng_name = any(s.countries)) or
			(c."name" = any(s.countries)));

ALTER TABLE "series"
DROP COLUMN "countries";