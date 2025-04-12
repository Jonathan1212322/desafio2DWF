-- Tabla Producto
CREATE TABLE Producto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    categoria VARCHAR(50) NOT NULL,
    stock INT NOT NULL CHECK (stock >= 0),
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla Cliente
CREATE TABLE Cliente (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    direccion VARCHAR(255),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla Pedido
CREATE TABLE Pedido (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cliente_id BIGINT NOT NULL,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'ENTREGADO', 'CANCELADO')),
    metodo_pago VARCHAR(50) NOT NULL CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA')),
    total DECIMAL(10,2) NOT NULL CHECK (total >= 0),
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id)
);

-- Tabla Pedido_Producto (intermedia)
CREATE TABLE Pedido_Producto (
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(10,2) NOT NULL CHECK (precio_unitario > 0),
    subtotal DECIMAL(10,2) NOT NULL CHECK (subtotal >= 0),
    PRIMARY KEY (pedido_id, producto_id),
    FOREIGN KEY (pedido_id) REFERENCES Pedido(id),
    FOREIGN KEY (producto_id) REFERENCES Producto(id)
);