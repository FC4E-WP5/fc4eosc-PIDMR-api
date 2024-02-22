-- ------------------------------------------------
-- Version: v3.0
--
-- Description: Migration that introduces the Provider, Regex and Provider_Action_Junction table
-- -------------------------------------------------

CREATE TABLE Provider (
   id BIGINT,
   type varchar(255) NOT NULL,
   name varchar(255) NOT NULL,
   description varchar(900) NOT NULL,
   metaresolver_id varchar(255) DEFAULT NULL,
   characters_to_be_removed INT DEFAULT 0,
   status smallint DEFAULT 1,
   PRIMARY KEY(id),
   UNIQUE (type),
   FOREIGN KEY (metaresolver_id) REFERENCES Metaresolver(id)
);

CREATE TABLE Regex (
   id SERIAL,
   regex varchar(255) NOT NULL,
   provider_id BIGINT NOT NULL,
   PRIMARY KEY(id),
   FOREIGN KEY (provider_id) REFERENCES Provider(id)
);

CREATE TABLE Provider_Action_Junction(
  provider_id BIGINT NOT NULL,
  action_id varchar(255) NOT NULL,
  PRIMARY KEY (provider_id, action_id),
  FOREIGN KEY (provider_id) REFERENCES Provider(id),
  FOREIGN KEY (action_id) REFERENCES Action(id)
);