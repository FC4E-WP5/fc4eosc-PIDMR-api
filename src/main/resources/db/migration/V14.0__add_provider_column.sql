-- ------------------------------------------------
-- Version: v14.0
--
-- Description: Migration that introduces a new Provider column
-- -------------------------------------------------

ALTER TABLE Provider
ADD COLUMN direct_resolution BOOLEAN DEFAULT false;

UPDATE Provider SET direct_resolution = true WHERE type = '21';

UPDATE Provider SET direct_resolution = true WHERE type = 'epic old';