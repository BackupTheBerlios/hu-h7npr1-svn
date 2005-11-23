# MySQL-Front Dump 2.4
#
# Host: localhost   Database: cijferlijst
#--------------------------------------------------------
# Server version 4.1.9-max


#
# Table structure for table 'cijferlijst'
#

DROP TABLE IF EXISTS cijferlijst;
CREATE TABLE `cijferlijst` (
  `CijferID` int(10) unsigned NOT NULL auto_increment,
  `StudentNaam` varchar(100) default NULL,
  `Cijfer` double(2,1) default '0.0',
  PRIMARY KEY  (`CijferID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



#
# Dumping data for table 'cijferlijst'
#
