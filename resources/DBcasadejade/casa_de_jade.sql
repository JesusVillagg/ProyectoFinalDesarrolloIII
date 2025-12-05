-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 05, 2025 at 04:14 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `casa_de_jade`
--

-- --------------------------------------------------------

--
-- Table structure for table `administrador`
--

CREATE TABLE `administrador` (
  `id_administrador` int(11) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `administrador`
--

INSERT INTO `administrador` (`id_administrador`, `usuario`, `password`) VALUES
(1, 'admin', 'admin123');

-- --------------------------------------------------------

--
-- Table structure for table `cliente`
--

CREATE TABLE `cliente` (
  `id_cliente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `correo` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cliente`
--

INSERT INTO `cliente` (`id_cliente`, `nombre`, `direccion`, `correo`, `password`) VALUES
(1, 'Roberto Gómez', 'Lomas de Chapultepec 101', 'r.gomez@mail.com', '12345'),
(2, 'Fernanda Castillo', 'Jardines del Pedregal 45', 'fer.castillo@mail.com', '12345'),
(3, 'Luis Miguel Torres', 'Av. Reforma 222', 'lm.torres@mail.com', '12345');

-- --------------------------------------------------------

--
-- Table structure for table `detalle_venta`
--

CREATE TABLE `detalle_venta` (
  `id_detalle` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `id_venta` int(11) NOT NULL,
  `id_presentacion` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `detalle_venta`
--

INSERT INTO `detalle_venta` (`id_detalle`, `cantidad`, `precio_unitario`, `id_venta`, `id_presentacion`) VALUES
(3, 1, 1200.00, 11, 7),
(5, 1, 1200.00, 13, 12),
(6, 4, 1200.00, 13, 13),
(8, 1, 1200.00, 15, 7),
(10, 1, 1200.00, 17, 10),
(11, 1, 1200.00, 18, 9);

-- --------------------------------------------------------

--
-- Table structure for table `presentacion`
--

CREATE TABLE `presentacion` (
  `id_presentacion` int(11) NOT NULL,
  `stock` int(11) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `id_producto` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `presentacion`
--

INSERT INTO `presentacion` (`id_presentacion`, `stock`, `precio`, `id_producto`) VALUES
(7, 46, 1200.00, 6),
(8, 30, 1200.00, 7),
(9, 39, 1200.00, 8),
(10, 33, 1200.00, 9),
(11, 45, 1200.00, 10),
(12, 24, 1200.00, 11),
(13, 51, 1200.00, 12),
(14, 30, 1200.00, 13);

-- --------------------------------------------------------

--
-- Table structure for table `producto`
--

CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `marca` varchar(50) DEFAULT NULL,
  `id_proveedor` int(11) NOT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  `imagen` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `producto`
--

INSERT INTO `producto` (`id_producto`, `nombre`, `marca`, `id_proveedor`, `precio`, `imagen`) VALUES
(6, 'Versace Eros', 'Versace', 3, 1200.00, 'versace_eros.png'),
(7, 'Born in Roma', 'Valentino', 1, 1200.00, 'born_in_roma.png'),
(8, 'Sauvage Dior', 'Dior', 1, 1200.00, 'sauvage_dior.png'),
(9, 'Miss Dior', 'Dior', 2, 1200.00, 'miss_dior.png'),
(10, 'Cloud Ariana', 'Ariana Grande', 3, 1200.00, 'cloud_ariana.png'),
(11, 'Tom Ford', 'Tom Ford', 1, 1200.00, 'tom_ford.png'),
(12, 'Nautica', 'Nautica', 2, 1200.00, 'nautica.png'),
(13, 'Carolina Herrera', 'Carolina Herrera', 2, 1200.00, 'carolina_herrera.png');

-- --------------------------------------------------------

--
-- Table structure for table `proveedor`
--

CREATE TABLE `proveedor` (
  `id_proveedor` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `proveedor`
--

INSERT INTO `proveedor` (`id_proveedor`, `nombre`, `telefono`, `direccion`, `correo`) VALUES
(1, 'EuroParfums Distribuidora', '55-1234-5678', 'Av. Masaryk 400, CDMX', 'contacto@europarfums.mx'),
(2, 'Luxe Scents Importadores', '81-8888-9999', 'Calle San Pedro 22, Monterrey', 'ventas@luxescents.com'),
(3, 'Global Fragance Supply', '33-4567-8901', 'Zona Andares 55, Guadalajara', 'supply@globalfragance.com'),
(5, 'Hola', '45-4564-4567', 'calle HOLA', 'hola@hola.mx');

-- --------------------------------------------------------

--
-- Table structure for table `venta`
--

CREATE TABLE `venta` (
  `id_venta` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `metodo_pago` varchar(50) NOT NULL,
  `direccion_entrega` varchar(255) DEFAULT NULL,
  `id_cliente` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `venta`
--

INSERT INTO `venta` (`id_venta`, `fecha`, `metodo_pago`, `direccion_entrega`, `id_cliente`) VALUES
(1, '2023-11-01 10:00:00', 'Tarjeta', 'Lomas de Chapultepec 101', 1),
(2, '2023-11-02 15:30:00', 'Efectivo', 'Tienda Física', 2),
(11, '2025-12-03 22:48:47', 'Efectivo/Depósito', 'Dirección Registrada', 1),
(13, '2025-12-03 22:55:48', 'Efectivo/Depósito', 'Dirección Registrada', 1),
(15, '2025-12-03 23:00:59', 'Efectivo/Depósito', 'Dirección Registrada', 1),
(17, '2025-12-04 12:46:22', 'Tarjeta Crédito/Débito', 'Dirección Registrada', 1),
(18, '2025-12-04 15:01:33', 'Tarjeta Crédito/Débito', 'Dirección Registrada', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `administrador`
--
ALTER TABLE `administrador`
  ADD PRIMARY KEY (`id_administrador`),
  ADD UNIQUE KEY `usuario` (`usuario`);

--
-- Indexes for table `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id_cliente`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- Indexes for table `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD PRIMARY KEY (`id_detalle`),
  ADD KEY `fk_detalle_venta` (`id_venta`),
  ADD KEY `fk_detalle_presentacion` (`id_presentacion`);

--
-- Indexes for table `presentacion`
--
ALTER TABLE `presentacion`
  ADD PRIMARY KEY (`id_presentacion`),
  ADD KEY `fk_presentacion_producto` (`id_producto`);

--
-- Indexes for table `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`),
  ADD KEY `fk_producto_proveedor` (`id_proveedor`);

--
-- Indexes for table `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id_proveedor`);

--
-- Indexes for table `venta`
--
ALTER TABLE `venta`
  ADD PRIMARY KEY (`id_venta`),
  ADD KEY `fk_venta_cliente` (`id_cliente`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `administrador`
--
ALTER TABLE `administrador`
  MODIFY `id_administrador` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id_cliente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `detalle_venta`
--
ALTER TABLE `detalle_venta`
  MODIFY `id_detalle` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `presentacion`
--
ALTER TABLE `presentacion`
  MODIFY `id_presentacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id_proveedor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `venta`
--
ALTER TABLE `venta`
  MODIFY `id_venta` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detalle_venta`
--
ALTER TABLE `detalle_venta`
  ADD CONSTRAINT `fk_detalle_presentacion` FOREIGN KEY (`id_presentacion`) REFERENCES `presentacion` (`id_presentacion`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `presentacion`
--
ALTER TABLE `presentacion`
  ADD CONSTRAINT `fk_presentacion_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `fk_producto_proveedor` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `venta`
--
ALTER TABLE `venta`
  ADD CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
