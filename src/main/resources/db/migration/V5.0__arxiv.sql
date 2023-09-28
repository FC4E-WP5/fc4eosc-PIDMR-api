-- ------------------------------------------------
-- Version: v5.0
--
-- Description: Migration that introduces the arxiv provider
-- -------------------------------------------------

INSERT INTO
      Provider(type, name, description, metaresolver_id)
VALUES
      ('arXiv','arXiv.','arXiv is a free distribution service and an open-access archive for 2,226,706 scholarly articles in the fields of physics, mathematics, computer science, quantitative biology, quantitative finance, statistics, electrical engineering and systems science, and economics.','HANDLER_MR');

SET @last_provider_id = LAST_INSERT_ID();

INSERT INTO
      Regex(regex, provider_id)
VALUES
      ('^(a|A)(r|R)(X|x)(i|I)(v|V):\\d{2}((9|0)[1-9]|1[0-2])\\.\\d{4,5}(v\\d+)?$', @last_provider_id),
      ('^(a|A)(r|R)(X|x)(i|I)(v|V):(astro-ph|cond-mat|gr-qc|hep-ex|hep-lat|hep-ph|hep-th|math-ph|nlin|nucl-ex|nucl-th|physics|quant-ph|math|CoRR|q-bio|q-fin|stat|eess|econ)(\\.[A-Z][A-Z])?\\/\\d{2}(0[1-9]|1[0-2])\\d+(v\\d+)?$', @last_provider_id);


INSERT INTO
      Provider_Action_Junction(provider_id, action_id)
VALUES
      (@last_provider_id, 'landingpage'),
      (@last_provider_id, 'metadata'),
      (@last_provider_id, 'resource');
