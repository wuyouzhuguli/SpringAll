/*
Navicat Oracle Data Transfer
Oracle Client Version : 11.2.0.1.0

Source Server         : localhost_test
Source Server Version : 110200
Source Host           : localhost:1521
Source Schema         : TEST

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2018-05-02 14:31:18
*/


-- ----------------------------
-- Table structure for STUDENT
-- ----------------------------
DROP TABLE STUDENT;
CREATE TABLE STUDENT (
SNO VARCHAR2(3 BYTE) NOT NULL ,
SNAME VARCHAR2(9 BYTE) NOT NULL ,
SSEX CHAR(2 BYTE) NOT NULL ,
DATASOURCE VARCHAR2(10 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Records of STUDENT
-- ----------------------------
INSERT INTO STUDENT VALUES ('001', 'KangKang', 'M ', 'oracle');
INSERT INTO STUDENT VALUES ('002', 'Mike', 'M ', 'oracle');
INSERT INTO STUDENT VALUES ('003', 'Jane', 'F ', 'oracle');
INSERT INTO STUDENT VALUES ('004', 'Maria', 'F ', 'oracle');

-- ----------------------------
-- Checks structure for table STUDENT
-- ----------------------------
ALTER TABLE STUDENT ADD CHECK (SNO IS NOT NULL);
ALTER TABLE STUDENT ADD CHECK (SNAME IS NOT NULL);
ALTER TABLE STUDENT ADD CHECK (SSEX IS NOT NULL);
