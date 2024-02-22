-- ------------------------------------------------
-- Version: v6.0
--
-- Description: Migration that introduces the swh provider
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
      (provider_id, 'swh','Software heritage persistent identifiers.','You can point to objects present in the Software Heritage archive by the means of SoftWare Heritage persistent IDentifiers, or SWHIDs for short, that are guaranteed to remain stable (persistent) over time.','HANDLER_MR');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^(s|S)(w|W)(h|H):[1-9]:(cnt|dir|rel|rev|snp):[0-9a-f]+(;(origin|visit|anchor|path|lines)=\\S+)*$', id FROM Provider WHERE type = 'swh';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'swh';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'swh';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'resource' FROM Provider WHERE type = 'swh';

END $$;