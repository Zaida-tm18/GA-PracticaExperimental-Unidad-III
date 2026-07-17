-- =========================================================
-- V3__datos_semilla.sql
-- PFC - UTEQ - Aplicaciones Web [111]
-- Paso 2.2 Seeder con >=50 registros realistas (GA punto 2b)
-- =========================================================

-- Categorias
INSERT INTO categorias (nombre, descripcion) VALUES ('Electronica', 'Dispositivos y componentes electronicos');
INSERT INTO categorias (nombre, descripcion) VALUES ('Hogar', 'Articulos para el hogar');
INSERT INTO categorias (nombre, descripcion) VALUES ('Deportes', 'Equipamiento deportivo');
INSERT INTO categorias (nombre, descripcion) VALUES ('Libros', 'Libros y material educativo');
INSERT INTO categorias (nombre, descripcion) VALUES ('Ropa', 'Vestimenta y accesorios');

-- Usuarios (password_hash = BCrypt de 'Password123!' generado en la app; aqui placeholder)
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES ('Ana Torres', 'ana.torres@uteq.edu.ec', '$2a$10$PLACEHOLDER_BCRYPT_HASH_REEMPLAZAR', 'ADMIN');
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES ('Luis Zambrano', 'luis.zambrano@uteq.edu.ec', '$2a$10$PLACEHOLDER_BCRYPT_HASH_REEMPLAZAR', 'USUARIO');
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES ('Maria Cedeno', 'maria.cedeno@uteq.edu.ec', '$2a$10$PLACEHOLDER_BCRYPT_HASH_REEMPLAZAR', 'USUARIO');
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES ('Carlos Mero', 'carlos.mero@uteq.edu.ec', '$2a$10$PLACEHOLDER_BCRYPT_HASH_REEMPLAZAR', 'USUARIO');
INSERT INTO usuarios (nombre, email, password_hash, rol) VALUES ('Jessica Ponce', 'jessica.ponce@uteq.edu.ec', '$2a$10$PLACEHOLDER_BCRYPT_HASH_REEMPLAZAR', 'ADMIN');

-- Entidades (55 registros >= 50 requeridos)
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Laptop', 'Descripcion detallada de laptop, articulo numero 1 del catalogo de prueba.', 577.28, 6, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Mouse inalambrico', 'Descripcion detallada de mouse inalambrico, articulo numero 2 del catalogo de prueba.', 668.68, 62, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Teclado mecanico', 'Descripcion detallada de teclado mecanico, articulo numero 3 del catalogo de prueba.', 204.77, 188, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Monitor 24"', 'Descripcion detallada de monitor 24", articulo numero 4 del catalogo de prueba.', 96.73, 189, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Silla ergonomica', 'Descripcion detallada de silla ergonomica, articulo numero 5 del catalogo de prueba.', 803.49, 22, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Escritorio', 'Descripcion detallada de escritorio, articulo numero 6 del catalogo de prueba.', 533.48, 8, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Balon de futbol', 'Descripcion detallada de balon de futbol, articulo numero 7 del catalogo de prueba.', 31.67, 55, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Raqueta de tenis', 'Descripcion detallada de raqueta de tenis, articulo numero 8 del catalogo de prueba.', 213.23, 154, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Bicicleta', 'Descripcion detallada de bicicleta, articulo numero 9 del catalogo de prueba.', 28.75, 50, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Casco deportivo', 'Descripcion detallada de casco deportivo, articulo numero 10 del catalogo de prueba.', 645.83, 179, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Novela de ciencia ficcion', 'Descripcion detallada de novela de ciencia ficcion, articulo numero 11 del catalogo de prueba.', 492.72, 56, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Libro de algoritmos', 'Descripcion detallada de libro de algoritmos, articulo numero 12 del catalogo de prueba.', 407.04, 71, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Manual de redes', 'Descripcion detallada de manual de redes, articulo numero 13 del catalogo de prueba.', 729.43, 1, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Cuaderno universitario', 'Descripcion detallada de cuaderno universitario, articulo numero 14 del catalogo de prueba.', 684.13, 40, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Calculadora cientifica', 'Descripcion detallada de calculadora cientifica, articulo numero 15 del catalogo de prueba.', 629.83, 87, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Camiseta deportiva', 'Descripcion detallada de camiseta deportiva, articulo numero 16 del catalogo de prueba.', 253.69, 55, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Pantalon jean', 'Descripcion detallada de pantalon jean, articulo numero 17 del catalogo de prueba.', 861.7, 86, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Chaqueta impermeable', 'Descripcion detallada de chaqueta impermeable, articulo numero 18 del catalogo de prueba.', 96.48, 97, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Zapatos deportivos', 'Descripcion detallada de zapatos deportivos, articulo numero 19 del catalogo de prueba.', 91.56, 88, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Gorra', 'Descripcion detallada de gorra, articulo numero 20 del catalogo de prueba.', 545.33, 11, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Audifonos bluetooth', 'Descripcion detallada de audifonos bluetooth, articulo numero 21 del catalogo de prueba.', 658.1, 137, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Parlante portatil', 'Descripcion detallada de parlante portatil, articulo numero 22 del catalogo de prueba.', 116.72, 96, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Camara web', 'Descripcion detallada de camara web, articulo numero 23 del catalogo de prueba.', 75.53, 75, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Disco duro externo', 'Descripcion detallada de disco duro externo, articulo numero 24 del catalogo de prueba.', 747.31, 158, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Memoria USB', 'Descripcion detallada de memoria usb, articulo numero 25 del catalogo de prueba.', 797.47, 92, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Router WiFi', 'Descripcion detallada de router wifi, articulo numero 26 del catalogo de prueba.', 521.72, 180, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Impresora laser', 'Descripcion detallada de impresora laser, articulo numero 27 del catalogo de prueba.', 67.25, 169, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Tablet', 'Descripcion detallada de tablet, articulo numero 28 del catalogo de prueba.', 208.97, 74, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Smartwatch', 'Descripcion detallada de smartwatch, articulo numero 29 del catalogo de prueba.', 886.76, 59, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Cargador portatil', 'Descripcion detallada de cargador portatil, articulo numero 30 del catalogo de prueba.', 780.49, 97, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Set de ollas', 'Descripcion detallada de set de ollas, articulo numero 31 del catalogo de prueba.', 253.78, 162, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Licuadora', 'Descripcion detallada de licuadora, articulo numero 32 del catalogo de prueba.', 751.52, 41, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Cafetera', 'Descripcion detallada de cafetera, articulo numero 33 del catalogo de prueba.', 336.31, 53, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Plancha', 'Descripcion detallada de plancha, articulo numero 34 del catalogo de prueba.', 604.8, 179, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Aspiradora', 'Descripcion detallada de aspiradora, articulo numero 35 del catalogo de prueba.', 843.3, 165, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Lampara de escritorio', 'Descripcion detallada de lampara de escritorio, articulo numero 36 del catalogo de prueba.', 68.9, 162, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Organizador de closet', 'Descripcion detallada de organizador de closet, articulo numero 37 del catalogo de prueba.', 158.17, 186, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Cortinas', 'Descripcion detallada de cortinas, articulo numero 38 del catalogo de prueba.', 224.1, 118, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Alfombra', 'Descripcion detallada de alfombra, articulo numero 39 del catalogo de prueba.', 344.61, 163, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Espejo decorativo', 'Descripcion detallada de espejo decorativo, articulo numero 40 del catalogo de prueba.', 620.9, 56, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Pelota de basquet', 'Descripcion detallada de pelota de basquet, articulo numero 41 del catalogo de prueba.', 617.72, 196, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Guantes de boxeo', 'Descripcion detallada de guantes de boxeo, articulo numero 42 del catalogo de prueba.', 699.51, 58, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Colchoneta de yoga', 'Descripcion detallada de colchoneta de yoga, articulo numero 43 del catalogo de prueba.', 740.51, 80, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Mancuernas', 'Descripcion detallada de mancuernas, articulo numero 44 del catalogo de prueba.', 364.04, 16, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Cinta de correr', 'Descripcion detallada de cinta de correr, articulo numero 45 del catalogo de prueba.', 193.83, 145, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Diccionario ingles-espanol', 'Descripcion detallada de diccionario ingles-espanol, articulo numero 46 del catalogo de prueba.', 789.34, 80, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Atlas geografico', 'Descripcion detallada de atlas geografico, articulo numero 47 del catalogo de prueba.', 195.3, 127, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Enciclopedia', 'Descripcion detallada de enciclopedia, articulo numero 48 del catalogo de prueba.', 359.09, 164, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Libro de historia', 'Descripcion detallada de libro de historia, articulo numero 49 del catalogo de prueba.', 415.67, 67, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Novela clasica', 'Descripcion detallada de novela clasica, articulo numero 50 del catalogo de prueba.', 129.97, 190, 1, 1);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Mochila escolar', 'Descripcion detallada de mochila escolar, articulo numero 51 del catalogo de prueba.', 507.42, 67, 2, 2);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Estuche de lapices', 'Descripcion detallada de estuche de lapices, articulo numero 52 del catalogo de prueba.', 673.57, 109, 3, 3);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Resma de papel', 'Descripcion detallada de resma de papel, articulo numero 53 del catalogo de prueba.', 808.54, 102, 4, 4);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Marcadores', 'Descripcion detallada de marcadores, articulo numero 54 del catalogo de prueba.', 328.98, 35, 5, 5);
INSERT INTO entidades (nombre, descripcion, precio, stock, categoria_id, usuario_id) VALUES ('Pizarra blanca', 'Descripcion detallada de pizarra blanca, articulo numero 55 del catalogo de prueba.', 461.02, 23, 1, 1);
