# Create new audit schema
CREATE DATABASE IF NOT EXISTS audit;
USE audit;

# Set up audit table
DROP TABLE IF EXISTS `lss_consent_history`;
CREATE TABLE `lss_consent_history` (
  `ID` int(1) NOT NULL AUTO_INCREMENT,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LINK_SUBJECT_STUDY_ID` int(11) NOT NULL,
  `CONSENT_TO_ACTIVE_CONTACT_ID` int(11) DEFAULT NULL,
  `CONSENT_TO_PASSIVE_DATA_GATHERING_ID` int(11) DEFAULT NULL,
  `CONSENT_TO_USE_DATA_ID` int(11) DEFAULT NULL,
  `CONSENT_STATUS_ID` int(11) DEFAULT NULL,
  `CONSENT_TYPE_ID` int(11) DEFAULT NULL,
  `CONSENT_DATE` date DEFAULT NULL,
  `CONSENT_DOWNLOADED` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS`consent_history`;
CREATE TABLE `consent_history` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `TIMESTAMP` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `LINK_SUBJECT_STUDY_ID` int(11) NOT NULL,
  `STUDY_COMP_ID` int(11) NOT NULL,
  `STUDY_COMP_STATUS_ID` int(11) NOT NULL,
  `CONSENT_STATUS_ID` int(11) NOT NULL,
  `CONSENT_TYPE_ID` int(11) NOT NULL,
  `CONSENT_DATE` date NOT NULL,
  `CONSENTED_BY` varchar(100) DEFAULT NULL,
  `COMMENTS` varchar(500) DEFAULT NULL,
  `REQUESTED_DATE` date DEFAULT NULL,
  `RECEIVED_DATE` date DEFAULT NULL,
  `COMPLETED_DATE` date DEFAULT NULL,
  `CONSENT_DOWNLOADED_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

# Set up view to see audit history data
USE study;
DROP VIEW IF EXISTS study.lss_consent_history;
DROP VIEW IF EXISTS study.consent_history;
CREATE VIEW study.lss_consent_history AS SELECT * FROM audit.lss_consent_history;
CREATE VIEW study.consent_history AS SELECT * FROM audit.consent_history;