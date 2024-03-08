-- ------------------------------------------------
-- Version: v12.0
--
-- Description: Migration that introduces the zenodo provider
-- -------------------------------------------------
DO $$

DECLARE provider_id bigint;

BEGIN

INSERT INTO
      ManageableEntity(entity_type)
VALUES
      ('Provider') returning id into provider_id;

INSERT INTO
      Provider(id, type, name, description, metaresolver_id)
VALUES
      (provider_id, '10.5281/zenodo','Zenodo.','Zenodo is a general-purpose open repository developed under the European OpenAIRE program and operated by CERN.','HANDLER_MR');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^10.5281\\/zenodo\.([0-9]{7})$', id FROM Provider WHERE type = '10.5281/zenodo';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = '10.5281/zenodo';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = '10.5281/zenodo';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'resource' FROM Provider WHERE type = '10.5281/zenodo';

END $$;