-- ------------------------------------------------
-- Version: v9.0
--
-- Description: Migration that introduces the epic old provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('epic old','epic old (5 digit ex 11500).','ePIC was founded in 2009 by a consortium of European partners in order to provide PID services for the European Research Community, based on the handle system (TM, https://www.handle.net/ ), for the allocation and resolution of persistent identifiers.','HANDLER');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^\\d{5,5}\\/.+$', @last_provider_id);