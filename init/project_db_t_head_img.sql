-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 121.36.37.230    Database: bilibili_db
-- ------------------------------------------------------
-- Server version	8.0.36-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_head_img`
--
USE secondkill_db;
DROP TABLE IF EXISTS `t_head_img`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `t_head_img` (
                              `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                              `user_id` int NOT NULL COMMENT '用户id',
                              `upload_time` timestamp NOT NULL COMMENT '上传时间',
                              `original_path` varchar(300) COLLATE utf8mb4_bin NOT NULL COMMENT '原始路径',
                              `mapping_path` varchar(300) COLLATE utf8mb4_bin NOT NULL COMMENT '映射路径',
                              `img_type` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '图片类型',
                              `img_size` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '图片大小 单位字节',
                              `original_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '文件原始名称',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户头像表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_head_img`
--

LOCK TABLES `t_head_img` WRITE;
/*!40000 ALTER TABLE `t_head_img` DISABLE KEYS */;
INSERT INTO `t_head_img` VALUES (1,14,'2024-05-21 04:33:57','D:/headImg/background.jpeg','/img/head/background.jpeg','jpeg','460508','background.jpeg'),(2,14,'2024-05-21 05:10:22','D:/headImg/linhan/20240521114714-1716268221584.jpg','/img/head/20240521114714-1716268221584.jpg','jpg','73597','20240521114714-1716268221584.jpg'),(3,14,'2024-05-21 05:14:33','D:/headImg/linhan/20240521114714-1716268472598.jpg','/img/head/20240521114714-1716268472598.jpg','jpg','73597','20240521114714-1716268472598.jpg'),(4,14,'2024-05-21 05:18:55','D:/headImg/linhan/20240521114714-1716268734898.jpg','/img/head/linhan/20240521114714-1716268734898.jpg','jpg','73597','20240521114714-1716268734898.jpg'),(5,14,'2024-05-21 05:53:36','D:/headImg/linhan/20240521114714-1716270815949.jpg','/img/head/linhan/20240521114714-1716270815949.jpg','jpg','73597','20240521114714-1716270815949.jpg'),(6,14,'2024-05-22 05:40:04','D:/headImg/linhan/QQ20240522133931-1716356404232.jpg','/img/head/linhan/QQ20240522133931-1716356404232.jpg','jpg','24765','QQ20240522133931-1716356404232.jpg'),(7,14,'2024-05-22 07:27:20','D:/headImg/linhan/QQ20240522133931-1716362839946.jpg','/user/img/linhan/QQ20240522133931-1716362839946.jpg','jpg','24765','QQ20240522133931-1716362839946.jpg'),(8,14,'2024-05-22 07:40:20','D:/headImg/linhan/QQ20240522133931-1716363620174.jpg','/img/head/linhan/QQ20240522133931-1716363620174.jpg','jpg','24765','QQ20240522133931-1716363620174.jpg');
/*!40000 ALTER TABLE `t_head_img` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-22 15:59:51
