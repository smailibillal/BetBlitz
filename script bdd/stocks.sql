CREATE DATABASE  IF NOT EXISTS `stocks`;
USE `stocks`;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `reference` int(11) NOT NULL,
  `designation` varchar(100) NOT NULL,
  `pu_ht` double NOT NULL,
  `qtestock` int(11) NOT NULL,
  PRIMARY KEY (`reference`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Datas for table `article`
--

INSERT INTO `article` VALUES (0,'Raquette de tennis',129.9,25);
