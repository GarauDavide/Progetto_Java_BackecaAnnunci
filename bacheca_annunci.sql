CREATE DATABASE  IF NOT EXISTS `bacheca_annunci` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `bacheca_annunci`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: bacheca_annunci
-- ------------------------------------------------------
-- Server version	5.6.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `annunci`
--

DROP TABLE IF EXISTS `annunci`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `annunci` (
  `idAnnuncio` int(10) NOT NULL AUTO_INCREMENT,
  `dataCompleta` datetime NOT NULL,
  `utente` char(50) NOT NULL,
  `tipologia` char(45) NOT NULL,
  `categoria` int(2) NOT NULL,
  `messaggio` text NOT NULL,
  PRIMARY KEY (`idAnnuncio`),
  KEY `categoria` (`categoria`),
  CONSTRAINT `annunci_ibfk_1` FOREIGN KEY (`categoria`) REFERENCES `categorie` (`idcategorie`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annunci`
--

LOCK TABLES `annunci` WRITE;
/*!40000 ALTER TABLE `annunci` DISABLE KEYS */;
INSERT INTO `annunci` VALUES (19,'2016-02-23 12:50:14','davide.garau89@gmail.com','Vendo Nuovo',3,'iPhone 6s. 16GB grigio siderale.'),(20,'2016-02-23 12:50:41','davide.garau89@gmail.com','Cerco Nuovo',7,'fdfadfadsfdfadsfasd'),(21,'2016-02-23 12:56:34','davide.garau89@gmail.com','Vendo Nuovo',11,'iPhone 6s');
/*!40000 ALTER TABLE `annunci` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorie`
--

DROP TABLE IF EXISTS `categorie`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categorie` (
  `idcategorie` int(11) NOT NULL AUTO_INCREMENT,
  `nomeCategoria` varchar(45) NOT NULL,
  PRIMARY KEY (`idcategorie`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorie`
--

LOCK TABLES `categorie` WRITE;
/*!40000 ALTER TABLE `categorie` DISABLE KEYS */;
INSERT INTO `categorie` VALUES (1,'Automobili'),(2,'Immobili'),(3,'Cellulari'),(4,'Computer'),(5,'Libri'),(6,'Film'),(7,'Elettronica'),(8,'Musica'),(9,'VideoGiochi'),(10,'Abbigliamento'),(11,'Sport');
/*!40000 ALTER TABLE `categorie` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `datidiagrammatorta`
--

DROP TABLE IF EXISTS `datidiagrammatorta`;
/*!50001 DROP VIEW IF EXISTS `datidiagrammatorta`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `datidiagrammatorta` (
  `categoria` tinyint NOT NULL,
  `quante` tinyint NOT NULL,
  `totalePost` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `datidiagrammatorta`
--

/*!50001 DROP TABLE IF EXISTS `datidiagrammatorta`*/;
/*!50001 DROP VIEW IF EXISTS `datidiagrammatorta`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `datidiagrammatorta` AS (select `b`.`nomeCategoria` AS `categoria`,count(`b`.`nomeCategoria`) AS `quante`,(select count(0) from `annunci`) AS `totalePost` from (`annunci` `a` join `categorie` `b` on((`a`.`categoria` = `b`.`idcategorie`))) group by `b`.`nomeCategoria`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-02-23 13:03:02
