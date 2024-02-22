-- ------------------------------------------------
-- Version: v7.0
--
-- Description: Migration that introduces the doi provider
-- -------------------------------------------------
DO $$

DECLARE provider_id bigint;

BEGIN

INSERT INTO
      ManageableEntity(entity_type)
VALUES
      ('Provider') returning id into provider_id;

INSERT INTO
      Provider(id, type, name, description, metaresolver_id, characters_to_be_removed)
VALUES
      (provider_id, 'doi','Digital Object Identifier.','DOI is an acronym for digital object identifier, meaning a digital identifier of an object. A DOI name is an identifier (not a location) of an entity on digital networks. It provides a system for persistent and actionable identification and interoperable exchange of managed information on digital networks. A DOI name can be assigned to any entity — physical, digital or abstract — primarily for sharing with an interested user community or managing as intellectual property. The DOI system is designed for interoperability; that is to use, or work with, existing identifier and metadata schemes. DOI names may also be expressed as URLs (URIs).','HANDLER', 4);

INSERT INTO
      Regex(regex, provider_id)
SELECT E'^(d|D)(o|O)(i|I):10\\.\\d+\\/.+$', id FROM Provider WHERE type = 'doi';

INSERT INTO
      Regex(regex, provider_id)
SELECT E'10\\.\\d+\\/.+$', id FROM Provider WHERE type = 'doi';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'landingpage' FROM Provider WHERE type = 'doi';

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
SELECT id, 'metadata' FROM Provider WHERE type = 'doi';

END $$;