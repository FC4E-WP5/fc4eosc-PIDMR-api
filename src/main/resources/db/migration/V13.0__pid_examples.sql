-- ------------------------------------------------
-- Version: v13.0
--
-- Description: Migration that introduces the pid examples
-- -------------------------------------------------

ALTER TABLE Provider
ADD COLUMN example VARCHAR(255) DEFAULT NULL;

UPDATE Provider SET example = 'ark:/13030/tf5p30086k' WHERE type = 'ark';
UPDATE Provider SET example = 'urn:nbn:de:hbz:6-85659524771' WHERE type = 'urn:nbn:de';
UPDATE Provider SET example = 'urn:nbn:fi-fe2021080942632' WHERE type = 'urn:nbn:fi';
UPDATE Provider SET example = 'arxiv:1512.00135' WHERE type = 'arXiv';
UPDATE Provider SET example = 'swh:1:cnt:94a9ed024d3859793618152ea559a168bbcbb5e2' WHERE type = 'swh';
UPDATE Provider SET example = '10.3352/jeehp.2013.10.3' WHERE type = 'doi';
UPDATE Provider SET example = '21.T11148/f5e68cc7718a6af2a96c' WHERE type = '21';
UPDATE Provider SET example = '11500/ATHENA-0000-0000-2401-6' WHERE type = 'epic old';
UPDATE Provider SET example = '10.5281/zenodo.8056361' WHERE type = '10.5281/zenodo';
