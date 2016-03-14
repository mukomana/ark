USE `pheno`;
--
-- Table structure for table `picked_pheno_dataset_category`
--
DROP TABLE IF EXISTS `picked_pheno_dataset_category`;
CREATE TABLE `picked_pheno_dataset_category` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `STUDY_ID` int(11) NOT NULL,
  `ARK_FUNCTION_ID` int(11) NOT NULL,
  `ARK_USER_ID` int(11) NOT NULL,
  `PHENO_DATASET_CATEGORY_ID` int(11) NOT NULL,
  `SELECTED` tinyint(4) NOT NULL DEFAULT '0',
  `ORDER_NUMBER` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `unique_picked_pheno_dataset_category` (`STUDY_ID`,`ARK_FUNCTION_ID`,`PHENO_DATASET_CATEGORY_ID`,`ARK_USER_ID`),
  KEY `fk_study` (`STUDY_ID`),
  KEY `fk_ark_function` (`ARK_FUNCTION_ID`),
  KEY `fk_pheno_dataset_category` (`PHENO_DATASET_CATEGORY_ID`),
  KEY `fk_user_id` (`ARK_USER_ID`),
  CONSTRAINT `FK_USER_ID_PICKED_PHENODATASET_CATEGORY` FOREIGN KEY (`ARK_USER_ID`) REFERENCES `study`.`ark_user` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_ARK_FUNCTION_PICKED_PHENODATASET_CATEGORY` FOREIGN KEY (`ARK_FUNCTION_ID`) REFERENCES `study`.`ark_function` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_PHENO_CATEGORY` FOREIGN KEY (`PHENO_DATASET_CATEGORY_ID`) REFERENCES `pheno_dataset_category` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_STUDY_PICKED_PHENODATASET_CATEGORY` FOREIGN KEY (`STUDY_ID`) REFERENCES `study`.`study` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=latin1;

