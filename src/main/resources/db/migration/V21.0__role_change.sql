-- ------------------------------------------------
-- Version: v21.0
--
-- Description: Migration that introduces the role change request table
-- -------------------------------------------------

CREATE TABLE RoleChangeRequest (
   id SERIAL,
   user_id varchar(255) NOT NULL,
   name varchar(255) NOT NULL,
   surname varchar(255) NOT NULL,
   role varchar(255) NOT NULL,
   description varchar(255) NOT NULL,
   email varchar(100) NOT NULL,
   requested_on timestamp NOT NULL,
   updated_on timestamp,
   updated_by varchar(255),
   status smallint NOT NULL,
   PRIMARY KEY (id)
);
