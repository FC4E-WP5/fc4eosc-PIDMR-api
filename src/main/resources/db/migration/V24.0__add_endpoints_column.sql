-- ------------------------------------------------
-- Version: v24.0
--
-- Description: Migration that introduces a new Provider_Action_Junction endpoints column
-- -------------------------------------------------

ALTER TABLE Provider_Action_Junction ADD COLUMN endpoints text[] DEFAULT NULL;
ALTER TABLE Provider_Action_Junction DROP COLUMN endpoint;


UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://n2t.net/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'ark');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://n2t.net/%s/?'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = 'ark');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://nbn-resolving.org/redirect/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://nbn-resolving.org/json/%s'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://nbn-resolving.org/json/%s'] WHERE action_id = 'resource' AND provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:de');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://urn.fi/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'urn:nbn:fi');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://arxiv.org/abs/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['http://export.arxiv.org/oai2?verb=GetRecord&metadataPrefix=oai_dc&identifier=oai:arXiv:org:%s'] WHERE action_id = 'metadata' and provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://arxiv.org/pdf/%s.pdf'] WHERE action_id = 'resource' AND provider_id = (SELECT id FROM Provider WHERE type = 'arXiv');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://orcid.org/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'orcid');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://zbmath.org/authors/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'zbMATH');
UPDATE Provider_Action_Junction SET endpoints = array['https://api.zbmath.org/v1/author/%s'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = 'zbMATH');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://ror.org/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'ROR');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://api.ror.org/v1/organizations/%s'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = 'ROR');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://zbmath.org/software/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = 'swMATH');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://api.zbmath.org/v1/software/%s'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = 'swMATH');

UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://zenodo.org/record/%s'] WHERE action_id = 'landingpage' AND provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://zenodo.org/api/records/%s'] WHERE action_id = 'metadata' AND provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');
UPDATE Provider_Action_Junction SET endpoints = ARRAY['https://zenodo.org/api/records/%s'] WHERE action_id = 'resource' AND provider_id = (SELECT id FROM Provider WHERE type = '10.5281/zenodo');


