-- ------------------------------------------------
-- Version: v4.0
--
-- Description: Migration that introduces the ark provider
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
      (provider_id, 'ark','ARK alliance.','Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects.','HANDLER_MR');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^(a|A)(r|R)(k|K):(?:\\/\\d{5,9})+\\/[a-zA-Z\\d]+(-[a-zA-Z\\d]+)*$', id FROM Provider WHERE type = 'ark';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'ark';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'ark';

END $$;