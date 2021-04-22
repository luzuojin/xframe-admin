CREATE TABLE IF NOT EXISTS `T_VERSION_DAT` (
  `Version` int NOT NULL PRIMARY KEY,
  `UpTime` timestamp NOT NULL
) DEFAULT CHARSET=utf8;

INSERT INTO `T_VERSION_DAT` VALUES ('0', now());

CREATE TABLE IF NOT EXISTS `T_ROLE` (
  `Id` int(11) NOT NULL PRIMARY KEY,
  `Name` varchar(64) NOT NULL,
  `Authorities` varchar(1024) NOT NULL,
  `Options` varchar(32) NOT NULL
) DEFAULT CHARSET=utf8;

INSERT INTO `T_ROLE` VALUES (1001, 'Admin', '_', '7');


CREATE TABLE IF NOT EXISTS `T_USER` (
  `Name` varchar(64) NOT NULL PRIMARY KEY,
  `Phone` varchar(64) NOT NULL,
  `Email` varchar(64) NOT NULL,
  `Passw` varchar(128) NOT NULL,
  `Roles` varchar(128) NOT NULL,
  `Ctime` timestamp NOT NULL
) DEFAULT CHARSET=utf8;

INSERT INTO `T_USER` VALUES ('admin', '10086', 'admin@xframe.dev', '21232f297a57a5a743894a0e4a801fc3', '1001', now());
INSERT INTO `T_USER` VALUES ('local', '10010', 'local@xframe.dev', '21232f297a57a5a743894a0e4a801fc3', '1001', now());