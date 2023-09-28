-- ------------------------------------------------
-- Version: v6.0
--
-- Description: Migration that introduces the swh provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('swh','Software heritage persistent identifiers.','You can point to objects present in the Software Heritage archive by the means of SoftWare Heritage persistent IDentifiers, or SWHIDs for short, that are guaranteed to remain stable (persistent) over time.','HANDLER_MR');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^(s|S)(w|W)(h|H):[1-9]:(cnt|dir|rel|rev|snp):[0-9a-f]+(;(origin|visit|anchor|path|lines)=\\S+)*$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage'),
      (@last_provider_id, 'metadata'),
      (@last_provider_id, 'resource');
