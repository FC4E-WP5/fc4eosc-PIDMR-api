-- ------------------------------------------------
-- Version: v20.0
--
-- Description: Migration that introduces a new Provider column called relies_on_dois
-- -------------------------------------------------

ALTER TABLE Provider ADD COLUMN relies_on_dois BOOLEAN DEFAULT false;

UPDATE Provider SET relies_on_dois = false;
