# Guía de entrevista — Angular + Java Assessment

Guía para el **entrevistador**. El repo es un pequeño gestor de tareas con
subtareas anidadas (árbol): backend **Spring Boot / Java 25 / Hibernate** y
frontend **Angular 22** (standalone + signals).

- **Rama `main`** → versión del candidato (4 métodos sin implementar, tests en rojo).
- **Rama `assessment`** → solución de referencia + esta guía.

El candidato completa **4 tareas** (2 back, 2 front) poniendo en verde los tests,
y luego se le hacen **preguntas de comprensión**. El objetivo es evaluar criterio
y comprensión real, no que memorice.

---

## Las 4 tareas y cómo evaluarlas

| # | Capa | Fichero / método | Qué evalúa |
|---|------|------------------|------------|
| 1 | Back | `TaskService.updateStatus` | Repo + `Optional` + excepción de dominio + mapeo |
| 2 | Back | `TaskService.getStats` | **Stream API** (`groupingBy` + `counting`) |
| 3 | Front | `TaskStore.changeStatus` | `Observable`/`subscribe` + refresco con **signals** |
| 4 | Front | `completionPercent` (`task.model.ts`) | **Recursión** sobre estructura anidada |

**Qué buscar en cada una:**

1. **`updateStatus`** — que cargue con `findById`, lance `TaskNotFoundException`
   si no existe (no un `null`), mute la entidad y persista. Bonus: que entienda
   que dentro de `@Transactional` el `save` puede ser redundante (dirty checking).
2. **`getStats`** — que use streams y no bucles manuales; que agrupe por el
   **valor de API** del enum (`apiValue()`), no por `name()`; que el `total`
   cuente todo el árbol.
3. **`changeStatus`** — que se **suscriba**, refresque en `next` (no antes) y
   gestione el `error`. Señal de alarma: olvidar el refresco o no manejar el error.
4. **`completionPercent`** — recursión correcta que incluye el nodo **y** todos
   sus descendientes; redondeo; inmutabilidad. Bonus: reusar el helper `flatten`.

---

## Preguntas de Backend (Java / Spring Boot / Hibernate)

### Arquitectura y capas
- **¿Por qué `controller → service → repository` y no meter la lógica en el controller?**
  Busca: separación de responsabilidades, testabilidad, transacciones en el servicio.
- **¿Por qué mapeamos a `TaskResponse` en vez de devolver la entidad `Task`?**
  Busca: no acoplar la API al modelo de persistencia, evitar exponer relaciones
  lazy / recursión infinita de Jackson, controlar el contrato.

### Records y DTOs
- **¿Por qué `record` para los DTOs?** Inmutabilidad, `equals`/`hashCode`/constructor
  gratis, intención clara de "objeto de transporte".
- **¿Un `record` puede ser una `@Entity` JPA?** No (JPA necesita constructor sin args
  y campos mutables/proxies) → por eso `Task` es una clase normal y los DTOs records.

### Enums + Jackson
- **¿Cómo llega `IN_PROGRESS` a la API como `"in_progress"`?** `@JsonValue` sobre
  `apiValue()` (serialización) y `@JsonCreator` (deserialización).
- **¿Dónde se guarda en BD?** `@Enumerated(EnumType.STRING)` → guarda el `name()`
  (`IN_PROGRESS`). Nota el desacople persistencia/API.

### Hibernate / JPA (núcleo del assessment)
- **¿Cómo se modela que una task tenga subtasks?** `@ManyToOne` **auto-referenciado**
  (`parent`) — lista de adyacencia; profundidad arbitraria.
- **¿Qué es el problema N+1 y dónde aparecería aquí?** Al mapear muchas tasks y
  tocar `project` o los hijos lazy uno a uno.
- **¿Cómo lo evitamos?** `@EntityGraph(attributePaths = "project")` para traer el
  proyecto en una query; y el **árbol se construye en memoria** desde una lista
  plana (`groupingBy` por `parentId`) en vez de navegar hijos lazy.
- **¿Por qué leer `parent.getId()` no dispara una query?** El id está en la FK; el
  proxy lazy lo conoce sin inicializarse.
- **Alternativas para cargar el árbol.** Busca que mencione recursive CTE
  (`WITH RECURSIVE`) y sus trade-offs frente a "traer todo y montar en memoria".

### Transacciones
- **¿Qué hace `@Transactional(readOnly = true)`?** Sin flush/dirty-check, hint al
  driver; para lecturas. **¿Y por qué `updateStatus` es de escritura?** Porque
  persiste el cambio.

### Streams
- **Explica `getStats`.** `groupingBy(clasificador, counting())` → mapa de conteos.
- **¿Por qué la clave del mapa es un `String` y no el enum?** Para un JSON estable
  en snake_case y evitar sutilezas de Jackson con claves enum.

### Manejo de errores
- **¿Qué es `ProblemDetail`?** RFC 7807, formato estándar de errores HTTP.
- **¿Cómo se traduce `TaskNotFoundException` a un 404?** `@RestControllerAdvice` +
  `@ExceptionHandler` centralizado (no try/catch disperso). ¿Y un body inválido? 400.

### Validación
- **¿Qué hace `@Valid` + `@NotNull` en `UpdateTaskStatusRequest`?** Valida el body
  antes de entrar al método; el advice devuelve 400 con los campos erróneos.

### Java moderno / concurrencia
- **Virtual threads: `spring.threads.virtual.enabled=true`. ¿Qué cambia?**
  Cada request en un hilo virtual barato; escala I/O sin reactive. Busca que
  distinga platform vs virtual y que mencione **thread-safety de los singletons**
  (no estado mutable en `@Service`).
- **¿Cuándo NO ayudan?** Trabajo CPU-bound.

### Spring: estereotipos y scopes
- **`@Service` vs `@Component` vs `@Repository`.** Todos son beans; `@Repository`
  añade traducción de excepciones; `@Service`/`@Component` son semánticos.
- **Scope por defecto.** Singleton. **¿Cómo harías uno no-singleton?** `@Scope("prototype")`.

### Persistencia de la demo
- **¿Por qué SQLite en memoria con Hikari `maximum-pool-size: 1`?** La BD en memoria
  vive dentro de una conexión; con una sola conexión persistente sobrevive toda la
  app. Buen tema de "conozco mi infraestructura".

---

## Preguntas de Frontend (Angular 22)

### Standalone y signals
- **¿Qué es un standalone component?** Sin `NgModule`; declara sus `imports`.
- **`signal`, `computed`. ¿`set` vs `update`?** `set` = valor absoluto; `update` =
  derivar del anterior (`update(v => !v)`). Ver `toggle()` y `completion`.
- **¿Por qué `computed` para el rollup?** Se recalcula solo cuando cambia la task.

### Componente recursivo (núcleo del assessment)
- **¿Cómo se dibuja un árbol de profundidad arbitraria?** `TaskNode` usa su propio
  selector `<app-task-node>` en su plantilla.
- **¿Hay que importarse a sí mismo en `imports`?** **No** — el componente ya está en
  su propio scope; hacerlo da el aviso "Component imports itself". (Error clásico.)

### Estado y DI
- **¿Por qué `TaskStore` va en `providers` del contenedor y no `providedIn: 'root'`?**
  Para que el contenedor y todos los nodos (descendientes en la jerarquía de
  inyectores) compartan **la misma instancia** con estado reactivo.
- **Singleton vs por-componente.** `providedIn: 'root'` = uno para toda la app;
  en `providers` de un componente = uno por instancia de ese componente.

### Control flow y HTTP
- **`@if` / `@for` con `track`.** Nuevo control flow; `track` para reconciliación
  eficiente de listas.
- **Explica el flujo de `changeStatus`.** `updateStatus()` (PUT) → `subscribe` →
  `load()` recarga el árbol. ¿Por qué recargar? Para reflejar el estado del server.

### Interceptor y mocks
- **¿Cómo funciona el front sin backend (StackBlitz)?** Un **interceptor funcional**
  (`withInterceptors`) intercepta `/api/*` y responde con datos en memoria; se activa
  con el flag `environment.useApiMocks`.
- **¿Por qué un interceptor y no modificar el `TaskService`?** El servicio y los
  componentes quedan **intactos**; el mock es transparente y se activa/desactiva sin
  tocar código de negocio.

### Recursión en TS
- **`completionPercent`.** Aplana el subárbol (`flatten`/`flatMap`), filtra `done`,
  redondea. Gemelo del `getStats` con streams del backend.

---

## Preguntas transversales (perfil senior)

- **El contrato back/front está en snake_case (`in_progress`).** ¿Cómo se garantiza
  que ambos lados coinciden? (Enum con `@JsonValue` en back; union de literales en
  front.) ¿Cómo lo endurecerías? (OpenAPI + generación de tipos.)
- **El árbol se monta en memoria.** ¿Hasta qué tamaño aguanta? ¿Cuándo pasarías a
  paginación / carga perezosa por nivel / recursive CTE?
- **Estrategia de tests.** Back: unit del servicio con Mockito, slice de controller
  (`@WebMvcTest`), `contextLoads`. Front: función pura (`completionPercent`) y store
  con `TaskService` mockeado. ¿Qué añadirían (e2e, componente recursivo)?
- **Si esto creciera:** ¿mover/reparentar tasks? (validar ciclos), ¿borrado en
  cascada?, ¿optimistic locking?

---

## Cómo arrancar para revisar

```bash
# Backend (requiere JDK 25 + Maven)
cd backend && mvn spring-boot:run        # http://localhost:8080/api/tasks  ·  Swagger: /api/swagger-ui.html

# Frontend
cd frontend && npm install && npm run dev # http://localhost:4200  (con useApiMocks=true no necesita backend)

# Tests
cd backend && mvn test
cd frontend && npm test
```
