<%-- 
    Document   : productos
    Created on : 13/10/2025, 9:17:47 p. m.
    Author     : juanm
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestión de Productos</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h2 {
            color: #007bff;
            font-weight: 700;
            text-align: center;
            margin-bottom: 25px;
        }
        .table thead {
            background-color: #cfe2ff;
        }
        .btn-primary {
            background-color: #007bff;
            border: none;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .form-label {
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container mt-4 mb-5">
    <h2>Gestión de Productos</h2>

    <!-- Mensajes -->
    <c:if test="${mensajeBean.hayInfo()}">
        <div class="alert alert-success text-center">${mensajeBean.textoInfo}</div>
    </c:if>
    <c:if test="${mensajeBean.hayError()}">
        <div class="alert alert-danger text-center">${mensajeBean.textoError}</div>
    </c:if>

    <!-- Formulario -->
    <div class="card p-4 mb-4">
        <h4 class="mb-3">${empty producto ? "Agregar Producto" : "Editar Producto"}</h4>

        <form action="productos" method="post" id="formProducto">
            <input type="hidden" name="accion" value="${empty producto ? 'crear' : 'actualizar'}">
            <c:if test="${not empty producto}">
                <input type="hidden" name="id" value="${producto.id}">
            </c:if>

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Código</label>
                    <input type="text" name="codigo" class="form-control" placeholder="Ej: P001"
                           value="${not empty producto ? producto.codigo : ''}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Nombre</label>
                    <input type="text" name="nombre" class="form-control" placeholder="Ej: Vela aromática"
                           value="${not empty producto ? producto.nombre : ''}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Categoría</label>
                    <input type="text" name="categoria" class="form-control" placeholder="Ej: Hogar"
                           value="${not empty producto ? producto.categoria : ''}">
                </div>
                <div class="col-md-3">
                    <label class="form-label">Precio</label>
                    <input type="number" step="0.01" name="precio" class="form-control" placeholder="Ej: 25000"
                           value="${not empty producto ? producto.precio : ''}" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Stock</label>
                    <input type="number" name="stock" class="form-control" placeholder="Ej: 10"
                           value="${not empty producto ? producto.stock : ''}" required>
                </div>
            </div>

            <div class="text-end mt-4">
                <button type="submit" class="btn btn-primary px-4">
                    <i class="bi bi-plus-circle"></i> ${empty producto ? "Agregar" : "Actualizar"}
                </button>
                <a href="productos" class="btn btn-secondary ms-2">Cancelar</a>
            </div>
        </form>
    </div>

    <!-- Tabla productos -->
    <div class="card p-4">
        <h5 class="mb-3">Lista de Productos</h5>
        <table id="tablaProductos" class="table table-striped table-bordered align-middle">
            <thead>
            <tr>
                <th>ID</th>
                <th>Código</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Stock</th>
                <th>Activo</th>
                <th>Acciones</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${productos}">
                <tr>
                    <td>${p.id}</td>
                    <td>${p.codigo}</td>
                    <td>${p.nombre}</td>
                    <td>${p.categoria}</td>
                    <td>${p.precio}</td>
                    <td>${p.stock}</td>
                    <td>
                        <c:choose>
                            <c:when test="${p.activo}"><span class="badge bg-success">Sí</span></c:when>
                            <c:otherwise><span class="badge bg-secondary">No</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-center">
                        <a href="productos?accion=editar&id=${p.id}" class="btn btn-warning btn-sm">Editar</a>
                        <form action="productos" method="post" style="display:inline;">
                            <input type="hidden" name="accion" value="eliminar">
                            <input type="hidden" name="id" value="${p.id}">
                            <button type="submit" class="btn btn-danger btn-sm"
                                    onclick="return confirm('¿Seguro que deseas eliminar este producto?');">
                                Eliminar
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<!-- Scripts -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js"></script>
<script src="https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.js"></script>
<script>
    $(document).ready(function () {
        $('#tablaProductos').DataTable({
            language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' }
        });
    });
</script>
</body>
</html>
