-- ------------------------------------------------
-- Version: v12.0
--
-- Description: Migration that introduces the zenodo provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('zenodo','Zenodo.','Zenodo is a general-purpose open repository developed under the European OpenAIRE program and operated by CERN.','HANDLER_MR');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^10.5281\\/zenodo\\.([0-9]{7})$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage'),
      (@last_provider_id, 'metadata'),
      (@last_provider_id, 'resource');
