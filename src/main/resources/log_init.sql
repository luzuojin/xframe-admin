CREATE TABLE IF NOT EXISTS `T_VERSION_LOG` (
  `Component` varchar(128) NOT NULL,
  `Version` int NOT NULL,
  `UpTime` timestamp NOT NULL,
  PRIMARY KEY(`Component`, `Version`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `T_OP_LOG` (
  `Name` varchar(64) NOT NULL,
  `Path` varchar(64) NOT NULL,
  `Params` varchar(1024) NOT NULL,
  `OpHost` varchar(32) NOT NULL,
  `OpMethod` varchar(16) NOT NULL,
  `OpTime` timestamp NOT NULL
) DEFAULT CHARSET=utf8;