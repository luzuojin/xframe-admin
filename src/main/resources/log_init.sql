CREATE TABLE `t_role` (
  `Id` int(11) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `Authorities` varchar(1024) NOT NULL,
  `ReadOnly` tinyint(1) NOT NULL,
  PRIMARY KEY (`Id`)
) DEFAULT CHARSET=utf8;