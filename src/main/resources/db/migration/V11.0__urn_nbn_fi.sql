-- ------------------------------------------------
-- Version: v11.0
--
-- Description: Migration that introduces the urn:nbn:fi provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('urn:nbn:fi','URN-NBN-FI.','The Finish URI in the urn:nbn: namespace.','HANDLER_MR');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^[U,u][R,r][N,n]:[N,n][B,b][N,n]:[F,f][I,i][a-z0-9()+,\\-.:=@;$_!*\'%\\/?#]+$', @last_provider_id);

INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage');