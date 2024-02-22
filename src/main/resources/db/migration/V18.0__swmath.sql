-- ------------------------------------------------
-- Version: v18.0
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
      (provider_id, 'swMATH','swMATH ID.','swMATH ID.','HANDLER_MR', '32212');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^([0-9]{1,5})$', id FROM Provider WHERE type = 'swMATH';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'swMATH';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'swMATH';

END $$;
