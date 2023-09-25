-- ------------------------------------------------
-- Version: v7.0
--
-- Description: Migration that introduces the doi provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id, characters_to_be_removed)
VALUES
      ('doi','Digital Object Identifier.','DOI is an acronym for digital object identifier, meaning a digital identifier of an object. A DOI name is an identifier (not a location) of an entity on digital networks. It provides a system for persistent and actionable identification and interoperable exchange of managed information on digital networks. A DOI name can be assigned to any entity — physical, digital or abstract — primarily for sharing with an interested user community or managing as intellectual property. The DOI system is designed for interoperability; that is to use, or work with, existing identifier and metadata schemes. DOI names may also be expressed as URLs (URIs).','HANDLER', 4);

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^(d|D)(o|O)(i|I):10\\.\\d+\\/.+$', @last_provider_id);