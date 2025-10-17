Proyecto Inventario Desarrollo de Aplicaciones Empresariales E191P 

Investigacion: 

¿Qué es un Managed Bean y en qué se diferencia de un JavaBean?

Un JavaBean es una clase de Java que sigue ciertos parámetros específicos: 
Tiene un constructor sin vacío. 
Sus atributos son privados
Tiene métodos getter y setter para acceder a esos atributos
Implementa Serializable. 

Es decir, es como un objeto de java, pero con estas reglas específicas, los JavaBeans son útiles para encapsular datos y se pueden usar en cualquier contexto de Java.
Por el contrario, Managed Bean, es un concepto que va relacionado con frameworks empresariales, especialmente en Java EE/Jakarta EE. Un Managed Bean es un objeto cuyo ciclo de vida es manejado por un contenedor, como puede ser el contenedor de CDI (Contexts and Dependency Injection) o JSF.
Las principales diferencias entre ambos son:
El JavaBean se instancia manualmente con la palabra new, por el un Managed Bean es creado y controlado automáticamente por el contenedor.
Contexto y alcance: Los Managed Beans tienen scopes específicos como @RequestScoped, @SessionScoped, @ApplicationScoped, mientras que los JavaBeans normales no tienen esta funcionalidad.
Inyección de dependencias: En un Managed Bean podemos usar anotaciones como @Inject para que el contenedor nos inyecte automáticamente otros beans. Con JavaBeans se manejan las dependencias manualmente.
Funcionalidad adicional: Los Managed Beans pueden tener interceptores, eventos, y otras características empresariales que un JavaBean simple no tiene.
En conclusión: Un JavaBean es una clase con ciertas convenciones de código, mientras que un Managed Bean es un componente empresarial cuyo ciclo de vida y dependencias son administrados automáticamente por un contenedor (CDI). Un Managed Bean generalmente cumple con las convenciones de JavaBean, pero le agrega toda la funcionalidad de gestión del framework. 
¿Para qué sirven @Named, @ApplicationScoped, @RequestScoped, @SessionScoped y @Dependent?

@Named: Esta anotación sirve para darle un nombre específico a un bean y hacerlo accesible desde la vista (como archivos XHTML en JSF). Cuando ponemos @Named en una clase, estamos diciéndole al contenedor que el bean puede ser referenciado por su nombre en las páginas web.
Por ejemplo: 
@Named("usuario") 
@SessionScoped 
public class UsuarioBean { ... }

@ApplicationScoped
Define que el bean vivirá durante toda la ejecución de la aplicación. Se crea una única instancia que es compartida por todos los usuarios y todas las sesiones. Es ideal para:
Configuraciones globales
Cachés de datos
Servicios que no mantienen estado específico de usuario
Básicamente existe desde que arranca la app hasta que se apaga el servidor.
@RequestScoped
El bean vive únicamente durante una petición HTTP. Se crea cuando llega el request y se destruye cuando se envía la respuesta. Cada petición obtiene una nueva instancia del bean. Es el scope por defecto en muchos casos y es útil cuando solo necesitas datos temporales para procesar esa petición específica.
@SessionScoped
El bean persiste durante toda la sesión del usuario. Se crea la primera vez que el usuario accede y se mantiene vivo hasta que la sesión expira (logout, timeout, etc.). Es útil para: 
Datos del usuario logueado
Carritos de compra
Preferencias temporales del usuario durante su navegación
Cada usuario tiene su propia instancia del bean.



@Dependent
El bean no tiene un scope independiente. Su ciclo de vida depende completamente del bean que lo inyecta, por ejemplo si se utiliza en un @SessionScoped, vivirá tanto como esa sesión, si se utiliza en un @RequestScoped, morirá con ese request.
Es útil cuando se tienen beans auxiliares que solo tienen sentido en el contexto de otro bean y no necesitan vivir independientemente.
Para tener en cuenta: 



### Jerarquía de Inyecciones:
```
ProductoServlet (@WebServlet, singleton por defecto)
    │
    ├─> ProductoFacade (@Named, @ApplicationScoped)
    │       │
    │       ├─> ProductoDAO (@Dependent)
    │       │       │
    │       │       └─> DataSource (del @Produces en DataSourceProducer)
    │       │
    │       └─> ValidadorProducto (@Dependent)
    │
    ├─> MensajeBean (@Named, @RequestScoped)
    │
    └─> PreferenciasBean (@Named, @SessionScoped, Serializable)


DataSourceProducer (@ApplicationScoped)
    └─> @Produces DataSource (del JNDI)



@Dependent
public class ValidadorProducto {
    public void validarCodigo(String codigo) throws Exception {
        // Sin estado, solo valida
    }
}
```

Aplicaciones de los scopes. 

@Dependent
public class ValidadorProducto {
    public void validarCodigo(String codigo) throws Exception {
        // Sin estado, solo valida
    }
}
```

**¿Dónde aplica en el flujo?**

**Escenario: Usuario crea un producto**
```
1. ProductoServlet recibe request
   ↓
2. Servlet inyecta ProductoFacade (@ApplicationScoped)
   ↓
3. Facade.crear(producto) se ejecuta
   ↓
4. Facade inyecta ValidadorProducto (@Dependent)
   → CDI crea NUEVA instancia de ValidadorProducto
   → Esta instancia vive solo durante esta operación
   ↓
5. Validador.validarProductoCompleto(p)
   ↓
6. Facade inyecta ProductoDAO (@Dependent)
   → CDI crea NUEVA instancia de ProductoDAO
   → Esta instancia vive solo durante esta operación
   ↓
7. DAO.insertar(p)
   → Obtiene Connection del pool
   → Ejecuta INSERT
   → Cierra Connection (try-with-resources)
   ↓
8. Al terminar Facade.crear():
   → Instancia de ValidadorProducto se destruye
   → Instancia de ProductoDAO se destruye



@RequestScoped - El Scope del Request HTTP

@Named("mensajeBean")
@RequestScoped
public class MensajeBean implements Serializable {
    private String textoError;
    private String textoInfo;
    
    public boolean hayError() {
        return textoError != null && !textoError.isEmpty();
    }
}
```

**¿Dónde aplica en el flujo?**

**Escenario 1: Usuario crea producto exitosamente**
```
REQUEST 1: POST /productos?accion=crear
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Servidor recibe request
   → CDI crea nueva instancia de MensajeBean
   
2. ProductoServlet.doPost() ejecuta
   ↓
3. crearProducto() se ejecuta
   → Producto se crea exitosamente
   ↓
4. mensajeBean.setTextoInfo("Producto creado exitosamente")
   → MensajeBean almacena el mensaje
   ↓
5. response.sendRedirect("productos")
   → Navegador hace nuevo GET
   
6. Request finaliza
   → MensajeBean se DESTRUYE automáticamente
   → textoInfo se pierde (mensaje flash)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

REQUEST 2: GET /productos (después del redirect)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Nuevo request = NUEVA instancia de MensajeBean
   → textoInfo = null (limpio)
   → textoError = null (limpio)
   
2. listarProductos() ejecuta
   → No hay mensajes pendientes
   
3. productos.jsp renderiza
   → No muestra alertas (hayInfo() = false)
```

**Escenario 2: Usuario intenta crear producto con error**
```
REQUEST: POST /productos?accion=crear
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. CDI crea MensajeBean para este request
   
2. crearProducto() ejecuta
   ↓
3. try {
      Producto p = new Producto();
      p.setCodigo("AB"); // Menos de 3 caracteres
      productoFacade.crear(p);
   } catch (Exception e) {
      mensajeBean.setTextoError("Error: El código debe tener al menos 3 caracteres");
      // NO hace redirect, hace forward
      request.getRequestDispatcher("/productos.jsp").forward(request, response);
   }
   
4. JSP renderiza en el MISMO request
   → MensajeBean todavía existe
   → hayError() = true
      
5. Request finaliza
   → MensajeBean se destruye

   
@SessionScoped 

@Named("preferenciasBean")
@SessionScoped
public class PreferenciasBean implements Serializable {
    private String idioma = "es";
    private Map<String, Object> filtros = new HashMap<>();
    
    public void setFiltro(String clave, Object valor) {
        filtros.put(clave, valor);
    }
}
```

#### **¿Dónde aplica en el flujo?**

**Escenario: Usuario navega y aplica filtros**
```
USUARIO - SESIÓN INICIADA 10:00 AM
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

REQUEST 1 (10:00 AM): GET /productos
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1. Primera vez que el usuario accede
   → CDI crea PreferenciasBean para el suario
   → idioma = "es" (default)
   → filtros = {} (vacío)
   
2. productos.jsp renderiza
   → Muestra todos los productos sin filtros


@ApplicationScoped

@Named("ProductoFacade")
@ApplicationScoped
public class ProductoFacade {
    @Inject
    private ProductoDAO dao;  // Inyección OK
    
    @Inject
    private ValidadorProducto validador;  // Inyección OK
    
    
    public void crear(Producto p) throws Exception {
        // OK: Sin estado, solo coordina
        validador.validarProductoCompleto(p);
        if (dao.existeCodigo(p.getCodigo())) {
            throw new Exception("Código duplicado");
        }
        dao.insertar(p);
    }
}
```

**¿Dónde aplica en el flujo?**


```
INSTANCIA ÚNICA DE ProductoFacade

Creada al iniciar el servidor
Compartida por TODOS los usuarios

├─ POST /productos?accion=crear
├─ ProductoServlet inyecta ProductoFacade
├─ CDI retorna LA INSTANCIA ÚNICA 
├─ facade.crear(ProductoA) INICIA
│  ├─ validador.validarProductoCompleto(ProductoA)
│  │  → Nueva instancia ValidadorProducto para el Usuario
│  ├─ dao.existeCodigo("P001")
│  │  → Nueva instancia ProductoDAO para Usuario
│  │  → Obtiene Connection del pool
│  │  → SELECT COUNT(*)... 
│  └─ dao.insertar(ProductoA)
│     → INSERT INTO productos...
└─ facade.crear() FINALIZA (5ms después)





   
