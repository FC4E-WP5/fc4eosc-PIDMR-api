-- ------------------------------------------------
-- Version: v16.0
--
-- Description: Migration that introduces the orcid provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id, example)
VALUES
      ('orcid','ORCID Identifier.','ORCID provides a persistent digital identifier (an ORCID iD) that you own and control, and that distinguishes you from every other researcher.','HANDLER_MR', '0000-0001-9547-1582');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^(\\d{4}-){3}\\d{3}(\\d|X|x)$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage');
