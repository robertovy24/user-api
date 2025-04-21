# User API

Este proyecto expone una API RESTful para el registro de usuarios, respaldada por una base de datos HSQLDB en memoria, autenticación JWT y documentación Swagger.

---

## 🛠️ Requisitos previos

- **Java 17** o superior  
- **Maven 3.6+**  
- (Opcional) Cliente HTTP (Postman)

---

## 🚀 Instalación y build

1. **Clonar el repositorio**  
   ```bash
   git clone <url-del-repositorio>
   cd user-api
   ```

2. **Compilar y empaquetar**  
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**  
   - Con Maven:
     ```bash
     mvn spring-boot:run
     ```

La aplicación arrancará por defecto en el puerto **8080**.

---

## 💾 Entidades (HSQLDB)

La aplicación usa una base de datos en memoria HSQLDB configurada en `application.properties`:

```properties
spring.datasource.url=jdbc:hsqldb:mem:userdb
spring.datasource.driverClassName=org.hsqldb.jdbcDriver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Tabla `users`

| Columna     | Tipo     | Descripción                             |
|-------------|----------|-----------------------------------------|
| `id`        | UUID     | PK, generado automáticamente            |
| `name`      | String   | Nombre completo del usuario             |
| `email`     | String   | Correo usuario           		   |
| `password`  | String   | Contraseña usuario 			   |
| `created`   | DateTime | Fecha de creación                       |
| `modified`  | DateTime | Fecha de última modificación            |
| `last_login`| DateTime | Fecha del último login                  |
| `token`     | String   | JWT generado con el correo 		   |
| `is_active` | Boolean  | Estado del usuario (activo/inactivo)    |

### Tabla `phones`

| Columna      | Tipo   | Descripción                                      |
|--------------|--------|--------------------------------------------------|
| `id`         | Long   | PK, generado automáticamente                     |
| `number`     | String | Número de teléfono                               |
| `citycode`   | String | Código de ciudad                                 |
| `contrycode` | String | Código de país                                   |
| `user_id`    | UUID   | FK → `users(id)` (relación @ManyToOne)           |

---

## 🔐 Autenticación JWT

La API genera un **token JWT** en el momento del registro y lo devuelve en la respuesta:

```json
{
  "id": "...",
  "created": "...",
  "modified": "...",
  "lastLogin": "...",
  "token": "<JWT>",
  "isActive": true,
  "phones": [ /* ... */ ]
}
```

La configuración en `application.properties` controla la firma y vigencia:

```properties
jwt.secret=mySecretKeyForJWTGeneration
jwt.expiration=3600000
```

La clase `cl.example.config.JwtUtil` se encarga de:

- Generar el token con `jwt.secret`
- Validar la firma y la fecha de expiración

---

## 🔑 Acceso JWT
El acceso al endpoint por swagger se entrega con la siguiente informacion 
- Usuario: user
- Password: se entrega en la consola, cuando se ejecuta el servidor de aplicaciones. ejemplo: 2d86d780-154f-43c1-be48-cb3327d93aad

```
Using generated security password: 2d86d780-154f-43c1-be48-cb3327d93aad
```
---

## 📄 Documentación Swagger

- **Interfaz interactiva (Swagger UI):**  
  ```
  http://localhost:8080/swagger-ui.html
  ```
  o
  ```
  http://localhost:8080/swagger-ui/index.html
  ```

- **Especificación JSON (OpenAPI):**  
  ```
  http://localhost:8080/v3/api-docs
  ```

---
## 📊 Diagrama 
  ```
  /user-api/src/main/resources/Diagrama.png
  ```

---

## 📚 Referencias útiles

- **Consola de HSQLDB (opcional):** no aplica H2 console, pues se usa HSQLDB.
- **Tests unitarios:** en `src/test/java` encontrará casos de prueba para registro de usuarios y JWT.

---
