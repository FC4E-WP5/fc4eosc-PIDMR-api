-- ------------------------------------------------
-- Version: v16.0
--
-- Description: Migration that introduces the zbMATH provider
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
      (provider_id, 'zbMATH','zbMATH author ID','Identifier of a person in the Zentralblatt MATH database.','HANDLER_MR', 'muller.stefan.1');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^[a-z][a-z\-]*(\.[a-z][a-z\-]*)?(\.[0-9]*)?$', id FROM Provider WHERE type = 'zbMATH';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'zbMATH';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'zbMATH';

END $$;
