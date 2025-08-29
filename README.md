# GLStock - Backend

API REST de gestión de inventario desarrollada con **Spring Boot** y **MySQL**.  
Incluye autenticación JWT, control de roles (ADMIN / GESTOR) y generación de reportes en PDF.

⚠️ **Estado:** este proyecto estuvo desplegado en **AWS EC2 + RDS** durante el verano de 2025, pero actualmente se conserva solo como portfolio y ejemplo de desarrollo.

## Funcionalidades principales
- CRUD de productos con imágenes
- Control de stock (entradas y salidas)
- Roles de usuario (ADMIN / GESTOR)
- Reportes PDF (todos, bajo stock, por categoría, movimientos)

## Endpoints principales
- `POST /auth/login` → autenticación con JWT
- `GET /productos` → listado de productos
- `POST /movimientos` → registrar entrada/salida
- `GET /reportes/productos/todos` → reporte PDF
