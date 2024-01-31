-- ------------------------------------------------
-- Version: v1.0
--
-- Description: Migration that introduces the Action table
-- -------------------------------------------------

CREATE TABLE Action (
   id varchar(255) NOT NULL,
   mode varchar(255) NOT NULL,
   name varchar(255) NOT NULL,
   PRIMARY KEY (id)
);

INSERT INTO
      Action(id, mode, name)
VALUES
      ('landingpage','landingpage','Landing Page'),
      ('metadata','metadata','Metadata'),
      ('resource','resource','Resource');