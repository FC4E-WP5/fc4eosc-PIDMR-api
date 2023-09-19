-- ------------------------------------------------
-- Version: v8.0
--
-- Description: Migration that introduces the epic provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('21','epic (21.*).','ePIC was founded in 2009 by a consortium of European partners in order to provide PID services for the European Research Community, based on the handle system (TM, https://www.handle.net/ ), for the allocation and resolution of persistent identifiers.','HANDLER');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^21\\.T?\\d+\\/.+$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage'),
      (@last_provider_id, 'metadata'),
      (@last_provider_id, 'resource');
