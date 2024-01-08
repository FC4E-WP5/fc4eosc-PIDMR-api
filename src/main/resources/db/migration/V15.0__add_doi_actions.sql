-- ------------------------------------------------
-- Version: v15.0
--
-- Description: Migration that introduces the doi actions
-- -------------------------------------------------

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'doi';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'doi';

INSERT INTO
      Regex(regex, provider_id)
SELECT '10\\.\\d+\\/.+$', id FROM Provider WHERE type = 'doi';

UPDATE Provider SET example = '10.3352/jeehp.2013.10.3' WHERE type = 'doi';