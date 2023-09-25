-- ------------------------------------------------
-- Version: v4.0
--
-- Description: Migration that introduces the ark provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('ark','ARK alliance.','Archival Resource Keys (ARKs) serve as persistent identifiers, or stable, trusted references for information objects.','HANDLER_MR');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^(a|A)(r|R)(k|K):(?:\\/\\d{5,9})+\\/[a-zA-Z\\d]+(-[a-zA-Z\\d]+)*$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage'),
      (@last_provider_id, 'metadata');
