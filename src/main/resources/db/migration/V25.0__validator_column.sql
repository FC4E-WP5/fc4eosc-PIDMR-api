-- ------------------------------------------------
-- Version: v25.0
--
-- Description: Migration that introduces a new Provider column
-- -------------------------------------------------

ALTER TABLE Provider ADD COLUMN validator smallint DEFAULT 0;

UPDATE Provider SET validator = 0;


