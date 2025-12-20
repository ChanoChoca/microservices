# Ejemplo de Microservicio con Spring Boot en 2026

**Objetivo**

Mostrar un ejemplo de microservicios actualizado hasta la fecha. Para ello he tomado estas desiciones:

- Usar Istio para usar API Gateway con JWT.
- Usar Keycloak para autenticación.
- Usar Strimzi para usar Kafka para comunicación entre servicios con bajo acoplamiento y alta escalabilidad.
- Test de Integración con Testcontainers y MockMvc de demostración en un servicio (Product).
- Swagger UI para documentar endpoints.
- Usar WebClient para comunicación entre servicios.
- No usar Open Feign ni Eureka.

![Keycloak Realms](/images/main.png)

Kubernetes en mi caso lo tengo instalado mediante [Docker Desktop](https://www.docker.com/products/docker-desktop/) con la configuración por defecto.

El flujo con Kafka es el siguiente:

- El Order Service publica un evento OrderPlaced.
- El Notification Service lo consume para enviar un email/SMS al cliente confirmando la compra.

## Pasos de instalación

- [Instalar Keycloak](#instalar-y-configurar-keycloak)
- [Instalar Istio](#instalar-istio)
- [Instalar Kafka](#instalar-strimzi-kafka)
- [Comandos servicios (microservicio)](#comandos-a-ejecutar-por-cada-servicio-ubicado-en-la-terminal-dentro-del-directorio)

## Comandos extras

- [Desinstalar Istio](#desinstalar-istio)
- [Deshacer Kafka](#deshacer-kafka)
- [Acceder a MySQL](#acceder-a-la-base-de-datos-mysql)
- [Ver registros de Kafka](#ver-registros-de-kafka-en-tiempo-real)
- [Ver pods](#ver-pods)
- [Ver registros de pod](#ver-registros-de-pod)

## Instalación

### Instalar y configurar Keycloak

Desplegamos MySQL:

```bash
kubectl apply -f mysql.yaml
```

Desplegamos Keycloak:

```bash
kubectl apply -f keycloak.yaml
```

Accedemos a Keycloak mediante navegador a **http://localhost:30080** con usuario **admin** y contraseña **admin**

En la pestaña **Manage realms** creamos un namespace llamado **microservices**:

![Keycloak Realms](/images/keycloak-1.png)

Creamos usuario en Keycloak (Nota: se debe estar ubicado sobre el real **microservices**):

![Keycloak Realms](/images/keycloak-2.png)

Le asignamos una contraseña:

![Keycloak Realms](/images/keycloak-3.png)

Creamos un cliente:

![Keycloak Realms](/images/keycloak-4.png)

Luego:

![Keycloak Realms](/images/keycloak-5.png)

Luego:

![Keycloak Realms](/images/keycloak-6.png)

### Instalar Istio

Descargar [Istio](https://github.com/istio/istio/releases). En mi caso he descargado el archivo **istio-1.28.1-win.zip**

Acceder a variables de entorno en tu pc ▶ Variables del sistema ▶ Path ▶ Agregar la ubicación bin del archivo

![Variables de entorno Windows](/images/istio.png)

Luego en Powershell (porque uso Windows):

```bash
istioctl manifest apply --set profile=demo
```

Modificar Istio (recuerda tener docker ejecutando al menos en segundo plano y haber instalado Kubernetes):

```bash
kubectl -n istio-system edit svc istio-ingressgateway
```

Se abrirá un bloc de notas probablemente, al final del archivo debes cambiar type: LoadBalancer a type: NodePort.

Recuerda guardar los cambios luego.

![Modificación de Istio](/images/istio-modify.png)

Por último aplicar el gateway:

```bash
kubectl apply -f gateway.yaml
```

### Instalar Strimzi (Kafka)

Crear namespace para Kafka:

```bash
kubectl create namespace kafka
```

Instalar Strimzi:

```bash
kubectl create -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```

Espera y verifica que los pods del operador estén arrancando (READY 1/1):

```bash
kubectl get pod -n kafka --watch
```

Despliegue del cluster Kafka:

```bash
kubectl apply -f https://strimzi.io/examples/latest/kafka/kafka-single-node.yaml -n kafka
```

Espera a que el cluster Kafka este listo, se notificará:

```bash
kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
```

### Comandos a ejecutar por cada servicio (ubicado en la terminal dentro del directorio)

Hacemos un build de cada app Spring Boot:

```bash
.\mvnw clean package -DskipTests
```

Creamos una imagen en Docker usando el build Spring Boot y el Dockerfile:

```bash
docker build -t product:latest .
```

Repetir casi lo mismo para los otros 3 servicios.

Desplegar en kubernetes los servicios:

```bash
kubectl apply -f order.yaml
```

Repetir casi lo mismo para los otros 3 manifiestos del microservicio.

### Hacer pruebas en Postman

Valores establecidos:

- Token Name: Token
- Grant type: Authorization Code (With PKCE)
- Callback URL: http://localhost:32598/oauth2/callback
- Auth URL: http://localhost:30080/realms/microservices/protocol/openid-connect/auth
- Access Token URL: http://localhost:30080/realms/microservices/protocol/openid-connect/token
- Client ID: api-gateway
- Code Challengue Method: SHA-256
- Client Authentication: Send as Basic Auth header

![Keycloak Realms](/images/postman.png)

Aprieta en el botón que se ubica debajo llamado 'Get New Access Token'. Se te abrirá un panel de login de keycloak, donde debes establecer los datos del usuario creado anteriormente. Luego aprieta en 'Use Token' y ya puedes ejecutar el endpoint sin errores.

Intenta probar lo mismo sin token también.

#### Documentación de endpoints de un servicio (también hay documentación para Order y para Inventory)

Pudes probarlo haciendo port-forward:

```bash
kubectl port-forward svc/product-service 8080:80
```

Luego accediendo a Swagger UI desde el Navegador:

```bash
http://localhost:8080/swagger-ui/index.html
```

- Nota: recomiendo en este caso ejecutar peticiones por Postman, no por Swagger.

## Comandos extras

### Desinstalar Istio

```bash
istioctl uninstall --purge
```

### Deshacer Kafka

```bash
kubectl delete -f https://strimzi.io/examples/latest/kafka/kafka-single-node.yaml -n kafka
```

Luego:

```bash
kubectl delete -f 'https://strimzi.io/install/latest?namespace=kafka' -n kafka
```

Luego:

```bash
kubectl delete pvc data-0-my-cluster-dual-role-0 -n kafka
```

### Acceder a la base de datos MYSQL

```bash
kubectl exec -it mysql-794ccc6665-rm9gm -- bash
```

Luego:

```bash
mysql -u root -p
```

### Ver registros de kafka en tiempo real

Entrar a un binarie, en mi caso es `my-cluster-dual-role-0`:

```bash
kubectl exec -it -n kafka my-cluster-dual-role-0 -- bash
```

Luego:

```bash
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic orders --from-beginning
```

### Ver pods

```bash
kubectl get pods -A
```

### Ver registros de pod

```bash
kubectl logs <pod>
```
