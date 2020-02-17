-- phpMyAdmin SQL Dump
-- version 3.4.3.2
-- http://www.phpmyadmin.net
--
-- Client: 127.0.0.1
-- Généré le : Ven 14 Juin 2013 à 14:24
-- Version du serveur: 5.5.15
-- Version de PHP: 5.3.8

-- \source C:\Users\gchandesris\Documents\workspace-sts-3.9.4.RELEASE\Spring_TP4_Annuaire\src\main\resources\sql\initbd.sql
-- \source src\main\resources\sql\initbd.sql
-- ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'mysqlpassword';


DROP TABLE IF EXISTS formation.t_adresse;
DROP TABLE IF EXISTS formation.t_personne;

DROP SCHEMA IF EXISTS formation;


--
-- Base de données: `oxiane-spring`
--

CREATE SCHEMA `formation` ;

USE formation;

-- --------------------------------------------------------

--
-- Structure de la table `t_personne`
--
CREATE TABLE IF NOT EXISTS `t_personne` (
  `puid` int(10) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `prenom` varchar(100) NOT NULL,
  PRIMARY KEY (`puid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




--
-- Structure de la table `t_adresse`
--
CREATE TABLE IF NOT EXISTS `t_adresse` (
  `puid` int(10) NOT NULL AUTO_INCREMENT,
  `ligne1` varchar(100) NOT NULL,
  `ligne2` varchar(100) NULL,
  `ville` varchar(100) NOT NULL,
  `code_postal` varchar(5) NOT NULL,
  `puid_personne` int(10) NOT NULL REFERENCES t_personne (puid),
  PRIMARY KEY (`puid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



INSERT INTO `t_personne` ( `nom`, `prenom`) VALUES
( 'Chombier', 'Michel');

INSERT INTO `t_personne` ( `nom`, `prenom`) VALUES
( 'Chirac', 'Jacques');

INSERT INTO `t_personne` ( `nom`, `prenom`) VALUES
( 'Dupont', 'Jean');



INSERT INTO `t_adresse` ( `ligne1`, `ligne2`, `ville`, `code_postal`, `puid_personne`) VALUES
( '98 av du gal leclerc', null, 'Boulogne', '92100', 1);

INSERT INTO `t_adresse` ( `ligne1`, `ligne2`, `ville`, `code_postal`, `puid_personne`) VALUES
( '98 av du gal leclerc', null,'Boulogne', '92100', 2);

INSERT INTO `t_adresse` (`ligne1`, `ligne2`, `ville`, `code_postal`, `puid_personne`) VALUES
( '98 av du gal leclerc', null, 'Boulogne', '92100', 3);
