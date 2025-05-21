-- ------------------------------------------------
-- Version: v28.0
--
-- Description: Migration that adds the Provider metadata_path column
-- -------------------------------------------------

ALTER TABLE provider
ADD COLUMN metadata_path jsonb DEFAULT '[]'::jsonb;
