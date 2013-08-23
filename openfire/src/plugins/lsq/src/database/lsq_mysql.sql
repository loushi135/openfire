DROP TABLE IF EXISTS `ofchatlogs`;
CREATE TABLE `ofchatlogs` (
  `MESSAGEID` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息id',
  `SESSIONJID` varchar(128) DEFAULT NULL COMMENT '用户session jid名称',
  `SENDER` varchar(128) DEFAULT NULL COMMENT '消息发送者',
  `RECEIVER` varchar(128) DEFAULT NULL COMMENT '接受者',
  `CREATEDATE` varchar(30) DEFAULT NULL COMMENT '消息发送、创建时间',
  `LENGTH` int(11) DEFAULT NULL COMMENT '消息长度、大小',
  `CONTENT` text COMMENT '消息内容',
  `DETAIL` text COMMENT '消息源报文',
  `STATE` int(11) DEFAULT NULL COMMENT '删除状态，1表示删除',
  PRIMARY KEY (`MESSAGEID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO ofVersion(name,version) values('lsq',1);