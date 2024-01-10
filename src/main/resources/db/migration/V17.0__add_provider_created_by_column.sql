-- ------------------------------------------------
-- Version: v17.0
--
-- Description: Migration that introduces new Provider columns
-- -------------------------------------------------

ALTER TABLE Provider
ADD COLUMN created_by varchar(255) DEFAULT NULL;

ALTER TABLE Provider
ADD COLUMN status tinyint DEFAULT 1;
