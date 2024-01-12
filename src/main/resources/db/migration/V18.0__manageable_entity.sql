-- ------------------------------------------------
-- Version: v18.0
--
-- Description: Migration that introduces a new Table
-- -------------------------------------------------

CREATE TABLE ManageableEntity (
   id BIGINT PRIMARY KEY,
   entity_type varchar(255) NOT NULL,
   created_by varchar(255) NULL
);

DELIMITER $$

CREATE PROCEDURE migrateProvidersToManageableEntityTable()
BEGIN

	DECLARE done BOOL DEFAULT false;
    DECLARE provider_id BIGINT;
    DECLARE provider_created_by varchar(255);

    DECLARE cur CURSOR FOR SELECT id, created_by FROM Provider;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = true;

	OPEN cur;

    process_provider: LOOP
        FETCH cur INTO provider_id, provider_created_by;
                  IF done = true THEN
			          LEAVE process_provider;
		          END IF;

		      INSERT INTO
		           ManageableEntity(id, entity_type, created_by)
		      VALUES(provider_id, 'Provider', provider_created_by);

	END LOOP;

	CLOSE cur;

END$$

DELIMITER ;

CALL migrateProvidersToManageableEntityTable;

DROP PROCEDURE IF EXISTS migrateProvidersToManageableEntityTable;

ALTER TABLE ManageableEntity DROP PRIMARY KEY;
ALTER TABLE ManageableEntity MODIFY COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY;

ALTER TABLE Provider DROP COLUMN created_by;

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE Provider MODIFY COLUMN id BIGINT;
SET FOREIGN_KEY_CHECKS = 1;