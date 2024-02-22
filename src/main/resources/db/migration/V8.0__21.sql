-- ------------------------------------------------
-- Version: v8.0
--
-- Description: Migration that introduces the epic provider
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
      (provider_id, '21','epic (21.*).','ePIC was founded in 2009 by a consortium of European partners in order to provide PID services for the European Research Community, based on the handle system (TM, https://www.handle.net/ ), for the allocation and resolution of persistent identifiers.','HANDLER');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^21\\.T?\\d+\\/.+$', id FROM Provider WHERE type = '21';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = '21';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = '21';

END $$;