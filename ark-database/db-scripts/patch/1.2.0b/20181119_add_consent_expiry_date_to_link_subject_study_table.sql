ALTER TABLE `study`.`link_subject_study` 
ADD COLUMN `CONSENT_EXPIRY_DATE` DATE NULL AFTER `CONSENT_DATE`;

ALTER TABLE `audit`.`aud_link_subject_study` 
ADD COLUMN `CONSENT_EXPIRY_DATE` DATE NULL AFTER `CONSENT_DATE`;
