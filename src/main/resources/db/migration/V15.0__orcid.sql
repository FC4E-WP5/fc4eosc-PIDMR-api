-- ------------------------------------------------
-- Version: v15.0
--
-- Description: Migration that introduces the orcid provider
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
      (provider_id, 'orcid','ORCID Identifier.','ORCID provides a persistent digital identifier (an ORCID iD) that you own and control, and that distinguishes you from every other researcher.','HANDLER_MR', '0000-0001-9547-1582');

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^(\\d{4}-){3}\\d{3}(\\d|X|x)$', id FROM Provider WHERE type = 'orcid';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'orcid';

END $$;
