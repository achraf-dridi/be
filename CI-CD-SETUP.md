# CI/CD Pipeline Setup with Jenkins

## Fichiers créés

- `Jenkinsfile` - Pipeline CI/CD
- `Dockerfile` - Image Docker pour l'application Spring Boot
- `Dockerfile.jenkins` - Image Jenkins personnalisée avec Docker et Maven
- `docker-compose.yml` - Orchestration des conteneurs

## Démarrage rapide

### 1. Démarrer Jenkins
```bash
docker-compose up -d jenkins
```

### 2. Récupérer le mot de passe initial Jenkins
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 3. Accéder à Jenkins
- URL: http://localhost:8080
- Coller le mot de passe récupéré

### 4. Configuration Jenkins

**Configurer les outils:**
1. Manage Jenkins → Tools
2. Maven installations:
   - Nom: `Maven-3.9`
   - Cocher "Install automatically"
3. JDK installations:
   - Nom: `JDK-21`
   - Cocher "Install automatically"

**Créer le pipeline:**
1. New Item → Pipeline
2. Nom: `backend-pipeline`
3. Pipeline → Definition: "Pipeline script from SCM"
4. SCM: Git
5. Repository URL: (votre repo Git)
6. Script Path: `Jenkinsfile`
7. Save

### 5. Build manuel de l'application (test)
```bash
# Build JAR
mvn clean package

# Build image Docker
docker build -t backend-app:latest .

# Run container
docker run -d --name backend -p 8081:8081 backend-app:latest

# Vérifier
curl http://localhost:8081/api/public/health
```

### 6. Lancer le pipeline Jenkins
1. Ouvrir le pipeline dans Jenkins
2. Cliquer "Build Now"
3. Suivre les logs

## Architecture

```
┌─────────────┐
│   Jenkins   │ (Port 8080)
│  Container  │
└──────┬──────┘
       │ Build & Deploy
       ↓
┌─────────────┐
│   Backend   │ (Port 8081)
│  Container  │
└─────────────┘
```

## Endpoints de l'application

- Application: http://localhost:8081
- Health: http://localhost:8081/api/public/health
- Swagger UI: http://localhost:8081/swagger-ui/index.html
- H2 Console: http://localhost:8081/h2-console

## Commandes utiles

```bash
# Voir les logs Jenkins
docker logs -f jenkins

# Voir les logs de l'application
docker logs -f backend

# Arrêter tout
docker-compose down

# Redémarrer Jenkins
docker-compose restart jenkins

# Rebuild Jenkins image
docker-compose build jenkins
docker-compose up -d jenkins
```

## Workflow du pipeline

1. **Checkout** - Récupère le code source
2. **Build** - Compile avec Maven
3. **Test** - Exécute les tests unitaires
4. **Build Docker Image** - Crée l'image Docker
5. **Deploy** - Déploie le container

## Troubleshooting

**Problème: Jenkins ne peut pas accéder à Docker**
```bash
# Vérifier les permissions
docker exec -it jenkins docker ps
```

**Problème: Port déjà utilisé**
```bash
# Changer les ports dans docker-compose.yml
ports:
  - "8082:8080"  # Jenkins
  - "8083:8081"  # Backend
```

**Problème: Build Maven échoue**
```bash
# Vérifier Java et Maven dans Jenkins
docker exec jenkins java -version
docker exec jenkins mvn -version
```
