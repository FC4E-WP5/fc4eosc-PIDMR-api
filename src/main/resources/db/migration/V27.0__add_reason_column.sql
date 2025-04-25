-- ------------------------------------------------
-- Version: v27.0
--
-- Description: Migration that adds the Provider reason column
-- -------------------------------------------------

ALTER TABLE ManageableEntity
ADD COLUMN reason varchar(255) DEFAULT NULL;
