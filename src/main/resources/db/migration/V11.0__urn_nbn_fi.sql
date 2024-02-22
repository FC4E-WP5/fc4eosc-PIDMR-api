-- ------------------------------------------------
-- Version: v11.0
--
-- Description: Migration that introduces the urn:nbn:fi provider
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
      (provider_id, 'urn:nbn:fi','URN-NBN-FI.','The Finish URI in the urn:nbn: namespace.','HANDLER_MR');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^[U,u][R,r][N,n]:[N,n][B,b][N,n]:[F,f][I,i][a-z0-9()+,\\-.:=@;$_!*\'%\\/?#]+$', id FROM Provider WHERE type = 'urn:nbn:fi';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'urn:nbn:fi';

END $$;