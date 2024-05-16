-- ------------------------------------------------
-- Version: v19.0
--
-- Description: Migration that introduces a new Provider_Action_Junction column
-- -------------------------------------------------

ALTER TABLE Provider_Action_Junction ADD COLUMN endpoint varchar(255) DEFAULT NULL;

UPDATE Provider_Action_Junction SET endpoint = 'https://n2t.net/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'ark');
UPDATE Provider_Action_Junction SET endpoint = 'https://n2t.net/%s/?' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'ark');

UPDATE Provider_Action_Junction SET endpoint = 'https://nbn-resolving.org/redirect/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');
UPDATE Provider_Action_Junction SET endpoint = 'https://nbn-resolving.org/json/%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');
UPDATE Provider_Action_Junction SET endpoint = 'https://nbn-resolving.org/json/%s' WHERE action_id = 'resource' and provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');

UPDATE Provider_Action_Junction SET endpoint = 'https://urn.fi/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:fi');

UPDATE Provider_Action_Junction SET endpoint = 'https://arxiv.org/abs/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');
UPDATE Provider_Action_Junction SET endpoint = 'http://export.arxiv.org/oai2?verb=GetRecord&metadataPrefix=oai_dc&identifier=oai:arXiv:org:%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');
UPDATE Provider_Action_Junction SET endpoint = 'https://arxiv.org/pdf/%s.pdf' WHERE action_id = 'resource' and provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');

UPDATE Provider_Action_Junction SET endpoint = 'https://orcid.org/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'orcid');

UPDATE Provider_Action_Junction SET endpoint = 'https://zbmath.org/authors/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'zbMATH');
UPDATE Provider_Action_Junction SET endpoint = 'https://api.zbmath.org/v1/author/%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'zbMATH');

UPDATE Provider_Action_Junction SET endpoint = 'https://ror.org/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'ROR');
UPDATE Provider_Action_Junction SET endpoint = 'https://api.ror.org/v1/organizations/%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'ROR');

UPDATE Provider_Action_Junction SET endpoint = 'https://zbmath.org/software/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = 'swMATH');
UPDATE Provider_Action_Junction SET endpoint = 'https://api.zbmath.org/v1/software/%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'swMATH');

UPDATE Provider_Action_Junction SET endpoint = 'https://zenodo.org/record/%s' WHERE action_id = 'landingpage' and provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');
UPDATE Provider_Action_Junction SET endpoint = 'https://zenodo.org/api/records/%s' WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');
UPDATE Provider_Action_Junction SET endpoint = 'https://zenodo.org/api/records/%s' WHERE action_id = 'resource' and provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');

