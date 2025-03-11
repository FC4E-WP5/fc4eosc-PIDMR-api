-- ------------------------------------------------
-- Version: v26.0
--
-- Description: Migration that changes the Provider endpoint column
-- -------------------------------------------------

ALTER TABLE Provider_Action_Junction ADD COLUMN endpoints_jsonb jsonb DEFAULT '[]';

UPDATE Provider_Action_Junction AS paj
SET endpoints_jsonb = subquery.new_endpoints
FROM (
    SELECT paj.provider_id, paj.action_id,
           jsonb_agg(jsonb_build_object('link', provider_endpoints, 'provider', p.type)) AS new_endpoints
    FROM Provider_Action_Junction paj
    JOIN Provider p ON paj.provider_id = p.id
    CROSS JOIN unnest(paj.endpoints) AS provider_endpoints
    GROUP BY paj.provider_id, paj.action_id, p.type
) AS subquery
WHERE paj.provider_id = subquery.provider_id
AND paj.action_id = subquery.action_id;


ALTER TABLE Provider_Action_Junction DROP COLUMN endpoints;
ALTER TABLE Provider_Action_Junction RENAME COLUMN endpoints_jsonb TO endpoints;

ALTER TABLE Provider ADD COLUMN examples TEXT[];

UPDATE Provider SET examples = ARRAY[example] WHERE example IS NOT NULL;

ALTER TABLE Provider DROP COLUMN example;
