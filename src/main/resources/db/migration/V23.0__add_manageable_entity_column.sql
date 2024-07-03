-- ------------------------------------------------
-- Version: v23.0
--
-- Description: Migration that introduces a new ManageableEntity column
-- -------------------------------------------------

ALTER TABLE ManageableEntity
ADD COLUMN status_updated_by varchar(255) DEFAULT NULL;
