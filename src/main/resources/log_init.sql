CREATE TABLE `T_VERSION` (
  `Version` int NOT NULL PRIMARY KEY,
  `UpTime` timestamp NOT NULL
) DEFAULT CHARSET=utf8;

INSERT INTO `T_VERSION` VALUES ('0', now());


CREATE TABLE `T_OP_LOG` (
  `Name` varchar(64) NOT NULL,
  `Path` varchar(64) NOT NULL,
  `Params` varchar(1024) NOT NULL,
  `OpHost` varchar(32) NOT NULL,
  `OpMethod` varchar(16) NOT NULL,
  `OpTime` timestamp NOT NULL
) DEFAULT CHARSET=utf8;