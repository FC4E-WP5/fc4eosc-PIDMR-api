-- ------------------------------------------------
-- Version: v14.0
--
-- Description: Migration that introduces a new Provider column
-- -------------------------------------------------

ALTER TABLE Provider
ADD COLUMN direct_resolution BOOLEAN DEFAULT false;

UPDATE Provider SET direct_resolution = true WHERE type = '21';

UPDATE Provider SET direct_resolution = true WHERE type = 'epic old';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'epic old';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'epic old';

DELETE FROM Provider_Action_Junction WHERE action_id = 'resource' AND provider_id IN (SELECT id FROM Provider WHERE type = '21');