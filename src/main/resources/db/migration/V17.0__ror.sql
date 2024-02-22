-- ------------------------------------------------
-- Version: v17.0
--
-- Description: Migration that introduces the ROR provider
-- -------------------------------------------------
DO $$

DECLARE provider_id bigint;

BEGIN

INSERT INTO
      ManageableEntity(entity_type)
VALUES
      ('Provider') returning id into provider_id;

INSERT INTO
      Provider(id, type, name, description, metaresolver_id, example)
VALUES
      (provider_id, 'ROR','Research Organization Registry.','ROR is a global, community-led registry of open persistent identifiers for research organizations.','HANDLER_MR', '05tcasm11');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^0[a-z|0-9]{6}[0-9]{2}$', id FROM Provider WHERE type = 'ROR';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'ROR';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'ROR';

END $$;
