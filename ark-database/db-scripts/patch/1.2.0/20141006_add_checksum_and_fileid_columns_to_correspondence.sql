ALTER TABLE `study`.`correspondences` 
ADD COLUMN `ATTACHMENT_CHECKSUM` VARCHAR(50) NULL AFTER `LINK_SUBJECT_STUDY_ID`,
ADD COLUMN `ATTACHMENT_FILE_ID` VARCHAR(1000) NULL AFTER `ATTACHMENT_CHECKSUM`;
