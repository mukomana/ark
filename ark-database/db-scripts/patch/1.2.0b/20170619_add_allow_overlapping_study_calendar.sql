ALTER TABLE `study`.`study_calendar` 
ADD COLUMN `ALLOW_OVERLAPPING` TINYINT(4) NOT NULL DEFAULT 0 AFTER `STUDY_COMPONENT_ID`;

