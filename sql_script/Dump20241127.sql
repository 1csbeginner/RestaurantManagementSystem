CREATE DATABASE  IF NOT EXISTS `restaurant` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `restaurant`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: restaurant
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `indent`
--

DROP TABLE IF EXISTS `indent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `indent` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `tablenumber` int DEFAULT NULL COMMENT '台子',
  `preview` varchar(2000) DEFAULT NULL COMMENT '预览',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `price` int DEFAULT NULL COMMENT '收入',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indent`
--

LOCK TABLES `indent` WRITE;
/*!40000 ALTER TABLE `indent` DISABLE KEYS */;
/*!40000 ALTER TABLE `indent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `picture` varchar(2000) DEFAULT NULL COMMENT '图片',
  `name` varchar(200) DEFAULT NULL COMMENT '名称',
  `introduce` varchar(200) DEFAULT NULL COMMENT '介绍',
  `price` double DEFAULT NULL COMMENT '价格',
  `vip_price` double DEFAULT NULL COMMENT 'vip价格',
  `is_deleted` tinyint unsigned DEFAULT '0' COMMENT '逻辑删除 1（true）已删除， 0（false）未删除',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `sort` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES (30,'http://localhost:8877/images/2024/11/17/32ca50c8913f4a1a83cb803bd830f0ad.png','红烧肉','肥瘦相间， 肥而不腻，香甜松软，营养丰富，入口即化',38,19,0,'2024-11-16 17:18:53','2024-11-16 17:18:53','荤菜'),(31,'http://localhost:8877/images/2024/11/17/00fc61a399a04e79a47d5adfb82b5f98.png','鱼香肉丝','色泽红润、富鱼香味，吃起来具有咸甜酸辣兼备的特点，肉丝质地柔滑软嫩，是下饭菜的必选。',28,14,0,'2024-11-16 17:18:53','2024-11-16 17:18:53','荤菜-素菜'),(32,'http://localhost:8877/images/2024/11/17/134853f3057a4ff4bb4bb218f0097fca.png','马卡龙','马卡龙是一种法式甜点，以其精致的外观、缤纷的色彩和轻盈的口感',68,40,0,'2024-11-17 01:36:02','2024-11-17 17:44:27','甜点'),(33,'http://localhost:8877/images/2024/11/17/e4da0184c47643bc8ee45491b11cfdda.png','佛跳墙','佛跳墙是一道福建传统名菜，以多种珍贵食材炖煮而成，味道鲜美，香气扑鼻。\r\n',88,44,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','汤'),(34,'http://localhost:8877/images/2024/11/17/1e43d29015534d7e84cee2406d8a68bb.png','包子','包子是一种中国传统面点，以面团包裹各种馅料，蒸熟后食用，外皮柔软，内馅多汁，风味多样。\r\n',3,1.5,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','主食'),(35,'http://localhost:8877/images/2024/11/17/4c1c9ebd8d14495a9ac0ca9cb04995c9.png','小鸡炖蘑菇','小鸡炖蘑菇是一道东北传统名菜，将鸡肉与蘑菇一同炖煮，肉质鲜嫩，蘑菇鲜美，汤汁浓郁，营养丰富。\r\n',49,24.5,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜-素菜'),(36,'http://localhost:8877/images/2024/11/17/4aa689995d324450a46c07399c0ba3a3.png','拍黄瓜','拍黄瓜是一道清新爽口的凉拌菜，将黄瓜拍松后加调料拌匀，口感爽脆，味道酸辣开胃。\r\n\r\n',12,6,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','凉菜-素菜'),(37,'http://localhost:8877/images/2024/11/17/796569fa207e42548bdf6a582a0c7e88.png','提拉米苏','是一种带咖啡酒味儿的意大利甜点,吃到嘴里香、滑、甜、腻，柔和中带有质感的变化',12.9,6.45,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','甜点'),(38,'http://localhost:8877/images/2024/11/17/f724c60508544258b8485a31ece4a518.png','泡芙','泡芙是一种法式甜点，以其蓬松的奶油面皮和丰富的奶油馅料而闻名，外酥内滑，口感轻盈',8,4,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','甜点'),(39,'http://localhost:8877/images/2024/11/17/c3ac515bd41c4d44b0ec4ee589808824.png','烧饼','烧饼是一种中国北方流行的传统面食，外皮酥脆，内里软绵，常夹有各种馅料，风味多样，既可单独食用也可搭配其他食物。\r\n',3,1.5,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','主食'),(40,'http://localhost:8877/images/2024/11/17/34c764b23b68425c8788a32294f948ce.png','猪肚鸡','猪肚鸡是一道广东特色菜，以其浓郁的汤汁、爽脆的猪肚和嫩滑的鸡肉而闻名，常辅以药材如白胡椒、党参等，具有很好的滋补效果。\r\n',68,34,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜-汤'),(41,'http://localhost:8877/images/2024/11/17/48ca3f1aa4114d1cbd45b7908e8179bf.png','番茄炒蛋','番茄炒蛋是一道简单易做的家常菜，以番茄和鸡蛋为主要食材，色泽鲜艳，酸甜可口，营养丰富。\r\n',12,6,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','素菜'),(42,'http://localhost:8877/images/2024/11/17/b0231b287bd4400dbf3749433d07c57d.png','皮蛋拌豆腐','皮蛋拌豆腐是一道清爽的中式凉菜，采用嫩豆腐与皮蛋搭配，淋上酱油和香油，口感滑嫩，风味独特。\r\n',12,6,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','素菜-凉菜'),(43,'http://localhost:8877/images/2024/11/17/5a70dbc5208d4b1f913baf5eed89d3a2.png','糖醋里脊','糖醋里脊是一道经典的中华菜肴，以猪里脊肉裹粉炸至金黄后浇上酸甜汁，色泽红亮，外酥里嫩，酸甜可口。\r\n',38,19,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜'),(44,'http://localhost:8877/images/2024/11/17/d0ed5f1bec894a8caa8c63403d6d9e84.png','紫菜蛋花汤','紫菜蛋花汤是一道简单快捷的家常汤品，以紫菜和鸡蛋为主要食材，汤色清澈，口感鲜美，营养丰富。\r\n',12,6,0,'2024-11-16 17:53:00','2024-11-22 13:21:58','汤'),(45,'http://localhost:8877/images/2024/11/17/f6dc5b5332f440bab884e12e82df31a1.png','红烧排骨','红烧排骨是一道经典的中式菜肴，以排骨为主料，经过煸炒、焖煮，色泽红亮，肉质酥软，味道鲜美。\r\n',48,24,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜'),(46,'http://localhost:8877/images/2024/11/17/7bb87fe1996945ae943e34c90f97089a.png','红烧茄子','红烧茄子是一道色香味俱全的家常菜，茄子经过油炸后加入调料烧制，外皮微酥，内里软嫩，味道鲜美。\r\n',18,9,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','素菜'),(47,'http://localhost:8877/images/2024/11/17/5731549affae43e5913a8601ac8d6e87.png','罗宋汤','罗宋汤是一道源自俄罗斯的西式汤品，以甜菜根为主要原料，色泽鲜艳，酸甜可口。\r\n',29,14.5,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','汤'),(48,'http://localhost:8877/images/2024/11/17/f7d16d3ab90b4ad09c770bfa54e9f117.png','苜蓿肉','苜蓿肉是一道以猪肉片与鸡蛋、木耳等混炒而成的传统名菜，因炒鸡蛋色黄而碎，类似木樨而得名，味道清新、营养丰富、口味鲜美、口感丰富、老少咸宜。\r\n',26,13,0,'2024-11-16 17:53:00','2024-11-20 14:41:43','荤菜-素菜'),(49,'http://localhost:8877/images/2024/11/17/3ddd1d84467a4069baef4d654374a370.png','蛋挞','蛋挞是一种以酥皮为底、蛋奶为馅的西式甜点，外皮松脆，内馅香甜滑嫩，口感层次丰富。\r\n',4,2,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','甜点'),(50,'http://localhost:8877/images/2024/11/17/d4fcfb32c8f246238afd708a4815623e.png','西湖醋鱼','西湖醋鱼是一道色泽红亮、肉质鲜嫩、酸甜清香的传统杭州名菜，以其独特的酸甜口感和鲜嫩的肉质而闻名。\r\n',88,44,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜'),(51,'http://localhost:8877/images/2024/11/17/89153f2e1c034e62bfa63ab05357f486.png','辣子鸡','辣子鸡是一道川菜，以鸡肉丁和大量的干辣椒为主料，色香味俱全，辣中带香，外酥里嫩。\r\n',38,19,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜'),(52,'http://localhost:8877/images/2024/11/17/6a78c106074643b6af1d6b4e7160eef0.png','锅包肉','锅包肉是一道东北特色菜肴，以猪里脊肉切片裹粉炸至金黄，再浇上酸甜汁翻炒，外酥里嫩，酸甜适口。\r\n',32,16,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','荤菜'),(53,'http://localhost:8877/images/2024/11/17/82010f6eb5a443b79c01a568ca4925cd.png','馒头','馒头是一种中国北方常见的面食，通常由发酵的面团制成，口感松软，味道清淡，可搭配各种菜肴食用。\r\n',1,0.5,0,'2024-11-16 17:53:00','2024-11-16 17:53:00','主食'),(54,'http://localhost:8877/images/2024/11/17/526f379e14f544aba88963e44ddb2fde.png','麻婆豆腐','麻婆豆腐是一道经典的川菜，以豆腐和牛肉末为主料，辅以麻辣调料烹饪，色泽红亮，麻辣鲜香，口感滑嫩。\r\n',12,6,0,'2024-11-16 17:53:00','2024-11-22 11:39:07','素菜'),(74,'http://localhost:8877/images/2024/11/21/d3cca00d8e2d43369d08ff2a6644fb08.png','剁椒鱼头','通常以鳙鱼鱼头、剁椒为主料，配以豉油、姜、葱、蒜等辅料蒸制而成。菜品色泽红亮、味浓、肉质细嫩。肥而不腻、口感软糯、鲜辣适口。',38,19,0,'2024-11-21 03:35:19','2024-11-27 10:08:34','荤菜'),(75,'http://localhost:8877/images/2024/11/21/466b5041237d4c459e0fd1e4904956ed.png','回锅肉','回锅肉，是四川传统菜式中家常菜肴的代表菜之一，其制作原料主要有猪后臀肉、青椒、蒜苗等',22,11,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜-荤菜'),(76,'http://localhost:8877/images/2024/11/21/23d9e677a13444c685cd86ef30b23fb5.png','孜然牛肉','孜然牛肉是一道以牛肉为主要食材，以孜然、香葱、生姜、干辣椒、花椒为调料制作而成的美食，口味咸鲜麻辣。\r\n',38,19,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(77,'http://localhost:8877/images/2024/11/21/764f9bee95234ee1b78d3c0d2908602b.png','干煸田鸡','鲜中带麻，麻中带辣，外酥里嫩，很开胃的一道菜。',38,19,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(78,'http://localhost:8877/images/2024/11/21/4216175745a14b6d8c786a68a9069d0c.png','水煮肉片','水煮肉片是以猪里脊肉为主料的一道地方新创名菜，吃时肉嫩菜鲜 ，汤红油亮，麻辣味浓，最宜下饭，为家常美食之一；特色是“麻、辣、鲜、香”。',28,14,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(79,'http://localhost:8877/images/2024/11/21/d183be47327a4325a0443c44a9f056aa.png','泡椒鳝鱼','泡椒鳝鱼是以鳝鱼、泡椒为主要原料制作的菜品，具有气血双补调理 、益智补脑调理的功效。',38,19,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(80,'http://localhost:8877/images/2024/11/21/62ff14cf0be6442e8ccaba6f40a445d9.png','海蛎豆腐汤','海蛎豆腐汤清淡新鲜，味道特别。',14,7,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','汤'),(81,'http://localhost:8877/images/2024/11/21/ccf0e5302f80454b86e11c2225e6ed08.png','火爆双脆','火爆双脆主料为猪肚和鸡肫，其口味特别，脆嫩爽口，咸鲜味美，',28,14,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(82,'http://localhost:8877/images/2024/11/21/b6dd43fdd86e482a864a45088a86ba72.png','炒田螺','炒田螺是一道由田螺、紫苏等材料制作而成的菜。田螺与一种叫紫苏的芳香草同炒，便会产生一种香中有辣，辣中带甜的怪味。',14,7,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(83,'http://localhost:8877/images/2024/11/21/66a8aee9821546e08d4cad0e8da065e6.png','炒苦瓜','炒苦瓜制作原料有苦瓜、小葱、盐等。具有清热消暑、养血益气、补肾健脾、滋肝明目的功效。',12,6,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜'),(84,'http://localhost:8877/images/2024/11/21/d6c5b70747fb450ea420ccac1e88e3cd.png','炒藕片','炒藕片，是以鲜藕、红灯笼椒为主料，盐、味精为辅料烹饪而成的菜品，含丰富的蛋白质、糖、钙、磷、铁和多种维生素。',14,7,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜'),(85,'http://localhost:8877/images/2024/11/21/43c0096b106a4218838c5869753a6b27.png','炒面','这是一份炒面，超级好吃，快来点！！！',6,3,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','主食'),(86,'http://localhost:8877/images/2024/11/21/5344222e36fa45a09393157d8a97ac21.png','白米饭','这是一份米饭，超级好吃，快来点！！！',3,1.5,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','主食'),(87,'http://localhost:8877/images/2024/11/21/23cf8522fe5943ba8d6047796f0ccbe4.png','酸辣土豆丝','酸辣土豆丝，是用土豆、辣椒、白醋，葱，姜等制作而成的菜肴，色泽光亮，酸辣可口。',12,6,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜'),(88,'http://localhost:8877/images/2024/11/21/10ef3d07cfdf4c21995819f9005177cc.png','金沙玉米','金沙玉米是一道美食，主要食材有玉米、咸蛋黄，调料为淀粉、料酒、食用油等，',25,12.5,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜'),(89,'http://localhost:8877/images/2024/11/21/235580aadf9d4fb4847d55414e8804e6.png','青椒肉丝','青椒肉丝，是以青椒为主要食材的家常菜，属于川菜菜系。口味香辣，菜品色香味俱全，操作简单，营养价值丰富。',22,11,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','素菜-荤菜'),(90,'http://localhost:8877/images/2024/11/21/bbb41e63401a4316be7632824316782c.png','香辣猪手','香辣猪蹄是一道美食，主要材料有猪蹄，配料有花椒、干红辣椒、葱、姜、蒜等。其味道香辣可口，肥而不腻。',38,19,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(91,'http://localhost:8877/images/2024/11/21/8ce6ed07e1b348af87c8029a1869fa94.png','香辣蟹','用肉蟹辅以葱、姜、花椒、干辣椒等制成的佳肴。本菜香、辣、鲜、脆，味道鲜美，营养丰富。',38,19,0,'2024-11-21 03:35:19','2024-11-21 03:35:19','荤菜'),(92,'http://localhost:8877/images/2024/11/21/d468e331a4914f6f91598e283d8e0c5a.png','鸡汁脆笋','鸡汁脆笋是一道由竹笋，高汤，猪油等做成的美食，烹饪工艺为炒。',22,11,0,'2024-11-21 03:35:19','2024-11-23 02:08:26','素菜');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistic`
--

DROP TABLE IF EXISTS `statistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistic` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `preview` varchar(2000) DEFAULT NULL COMMENT '预览',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `createyear` int DEFAULT NULL,
  `createmonth` int DEFAULT NULL,
  `createday` int DEFAULT NULL,
  `earning` int DEFAULT NULL,
  `sort` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账目信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistic`
--

LOCK TABLES `statistic` WRITE;
/*!40000 ALTER TABLE `statistic` DISABLE KEYS */;
/*!40000 ALTER TABLE `statistic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序号',
  `picture` varchar(2000) DEFAULT NULL COMMENT '头像',
  `name` varchar(200) NOT NULL COMMENT '名称',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `introduce` varchar(200) DEFAULT NULL COMMENT '个人简介',
  `is_vip` tinyint unsigned DEFAULT '0' COMMENT '逻辑删除 1（true）已删除， 0（false）未删除',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `is_manager` tinyint unsigned DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,NULL,'admin','111111',NULL,0,'2024-11-09 16:43:20','2024-11-15 22:20:06',1),(2,NULL,'4444','123456',NULL,0,'2024-11-09 16:43:51','2024-11-20 15:15:50',1),(3,NULL,'1111','111111',NULL,1,'2024-11-09 16:45:53','2024-11-15 21:05:21',0),(10,NULL,'555','111111',NULL,0,'2024-11-21 11:52:09','2024-11-21 11:52:09',0),(11,NULL,'5555','111111',NULL,0,'2024-11-21 12:02:14','2024-11-21 12:02:14',0),(12,NULL,'777','111111',NULL,0,'2024-11-22 09:56:36','2024-11-22 09:56:36',0),(13,NULL,'0000','111111',NULL,0,'2024-11-27 09:51:28','2024-11-27 09:51:28',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-27 11:09:30
