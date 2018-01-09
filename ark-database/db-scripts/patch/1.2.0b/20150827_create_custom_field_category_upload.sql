--
-- Table structure for table `custom_field_category_upload`
--
use study;
DROP TABLE IF EXISTS `custom_field_category_upload`;
CREATE TABLE `custom_field_category_upload` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `CUSTOM_FIELD_CATEGORY_ID` int(11) NOT NULL,
  `UPLOAD_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CFCU_CUSTOM_FIELD_CATEGORY_ID` (`CUSTOM_FIELD_CATEGORY_ID`),
  KEY `FK_CFCU_UPLOAD_ID` (`UPLOAD_ID`),
  CONSTRAINT `FK_CFCU_CUSTOM_FIELD_CATEGORY_ID` FOREIGN KEY (`CUSTOM_FIELD_CATEGORY_ID`) REFERENCES `custom_field_category` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_CFCU_UPLOAD_ID` FOREIGN KEY (`UPLOAD_ID`) REFERENCES `upload` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
