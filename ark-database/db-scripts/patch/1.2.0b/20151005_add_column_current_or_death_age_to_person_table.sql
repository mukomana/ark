ALTER TABLE `study`.`person` 
ADD COLUMN `CURRENT_OR_DEATH_AGE` VARCHAR(50) NULL AFTER `PREFERRED_EMAIL_STATUS`;

ALTER TABLE `audit`.`aud_person` 
ADD COLUMN `CURRENT_OR_DEATH_AGE` VARCHAR(50) NULL AFTER `VITAL_STATUS_ID`;


