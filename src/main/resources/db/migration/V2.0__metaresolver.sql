-- ------------------------------------------------
-- Version: v2.0
--
-- Description: Migration that introduces the Metaresolver table
-- -------------------------------------------------

CREATE TABLE Metaresolver (
   id varchar(255) NOT NULL,
   location varchar(255) NOT NULL,
   description varchar(255) NOT NULL,
   PRIMARY KEY (id)
);

INSERT INTO
      Metaresolver(id, location, description)
VALUES
      ('FC4EOSC','http://hdl.handle.net/21.T11999/METARESOLVER@','The FC4EOSC Metaresolver.'),
      ('HANDLER','http://hdl.handle.net/','The Handle.Net Metaresolver.'),
      ('HANDLER_MR','http://hdl.handle.net/21.T11973/MR@','The Handle.Net Metaresolver.');

CREATE TABLE ManageableEntity (
   id SERIAL,
   entity_type varchar(255) NOT NULL,
   created_by varchar(255) DEFAULT NULL,
   PRIMARY KEY (id)
);