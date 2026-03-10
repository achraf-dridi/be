# Comment Jenkins lit le Jenkinsfile dans un container

## 1. Changement de port Jenkins

Jenkins tourne maintenant sur le port **9090** au lieu de 8080:
```yaml
ports:
  - "9090:8080"  # Host:Container
```

Accès: **http://localhost:9090**

---

## 2. Comment Jenkins lit le Jenkinsfile ?

### Méthode 1: Via Git (Recommandé pour production)

```
┌─────────────────────────────────────────────────────┐
│  1. Vous créez un pipeline dans Jenkins UI          │
│     - Pipeline script from SCM                      │
│     - Repository: https://github.com/user/repo.git  │
│     - Script Path: Jenkinsfile                      │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│  2. Jenkins clone le repo Git                       │
│     git clone https://github.com/user/repo.git      │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│  3. Jenkins lit Jenkinsfile depuis le repo cloné    │
│     /var/jenkins_home/workspace/pipeline/Jenkinsfile│
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│  4. Jenkins exécute les stages du pipeline          │
└─────────────────────────────────────────────────────┘
```

**Configuration dans Jenkins:**
1. New Item → Pipeline
2. Pipeline section:
   - Definition: "Pipeline script from SCM"
   - SCM: Git
   - Repository URL: `https://github.com/your-username/your-repo.git`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`

---

### Méthode 2: Via Volume monté (Pour tests locaux)

```yaml
volumes:
  - .:/workspace  # Monte le dossier actuel dans /workspace
```

```
┌─────────────────────────────────────────────────────┐
│  Votre machine (Windows)                            │
│  C:\...\backend\                                    │
│  ├── Jenkinsfile                                    │
│  ├── pom.xml                                        │
│  └── src/                                           │
└─────────────────────────────────────────────────────┘
                        ↓ Volume mount
┌─────────────────────────────────────────────────────┐
│  Container Jenkins                                  │
│  /workspace/                                        │
│  ├── Jenkinsfile  ← Accessible en temps réel       │
│  ├── pom.xml                                        │
│  └── src/                                           │
└─────────────────────────────────────────────────────┘
```

**Configuration dans Jenkins:**
1. New Item → Pipeline
2. Pipeline section:
   - Definition: "Pipeline script from SCM"
   - SCM: Git
   - Repository URL: `file:///workspace` (chemin local)
   - OU
   - Definition: "Pipeline script"
   - Copier/coller le contenu du Jenkinsfile

---

### Méthode 3: Pipeline script direct (Pour tests rapides)

```
┌─────────────────────────────────────────────────────┐
│  Jenkins UI                                         │
│  Pipeline → Definition: "Pipeline script"           │
│  ┌───────────────────────────────────────────────┐  │
│  │ pipeline {                                    │  │
│  │   agent any                                   │  │
│  │   stages {                                    │  │
│  │     stage('Build') { ... }                    │  │
│  │   }                                           │  │
│  │ }                                             │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

Vous copiez/collez directement le Jenkinsfile dans l'interface Jenkins.

---

## 3. Flux complet avec Git (Production)

```
┌──────────────┐
│  Developer   │
│  Push code   │
└──────┬───────┘
       │
       ↓
┌──────────────┐
│  GitHub/Git  │
│  Repository  │
│  - Jenkinsfile
│  - pom.xml
│  - src/
└──────┬───────┘
       │
       ↓ Jenkins poll/webhook
┌──────────────────────────────────────┐
│  Jenkins Container                   │
│                                      │
│  1. git clone repo                   │
│     → /var/jenkins_home/workspace/   │
│                                      │
│  2. Lit Jenkinsfile                  │
│                                      │
│  3. Exécute stages:                  │
│     - Checkout ✓                     │
│     - Build (mvn package) ✓          │
│     - Test (mvn test) ✓              │
│     - Docker build ✓                 │
│     - Deploy ✓                       │
└──────────────────────────────────────┘
       │
       ↓
┌──────────────┐
│  Backend     │
│  Container   │
│  Running     │
└──────────────┘
```

---

## 4. Workspace Jenkins

Quand Jenkins exécute un pipeline:

```
/var/jenkins_home/
├── workspace/
│   └── backend-pipeline/      ← Votre projet cloné ici
│       ├── Jenkinsfile        ← Jenkins lit ce fichier
│       ├── pom.xml
│       ├── src/
│       └── target/            ← Artifacts générés
├── jobs/
│   └── backend-pipeline/
│       └── config.xml         ← Configuration du pipeline
└── secrets/
    └── initialAdminPassword
```

---

## 5. Configuration recommandée pour votre projet

### Étape 1: Push vers Git
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/your-username/backend.git
git push -u origin main
```

### Étape 2: Créer pipeline dans Jenkins
1. Accéder à http://localhost:9090
2. New Item → "backend-pipeline" → Pipeline
3. Configuration:
   ```
   Pipeline script from SCM
   SCM: Git
   Repository URL: https://github.com/your-username/backend.git
   Branch: */main
   Script Path: Jenkinsfile
   ```
4. Save

### Étape 3: Build
- Cliquer "Build Now"
- Jenkins va:
  1. Cloner votre repo
  2. Lire le Jenkinsfile
  3. Exécuter les stages

---

## 6. Vérification

```bash
# Voir le workspace Jenkins
docker exec jenkins ls -la /var/jenkins_home/workspace/

# Voir le Jenkinsfile dans Jenkins
docker exec jenkins cat /var/jenkins_home/workspace/backend-pipeline/Jenkinsfile

# Voir les logs du build
docker logs jenkins
```

---

## Résumé

| Méthode | Jenkinsfile location | Usage |
|---------|---------------------|-------|
| **Git SCM** | GitHub/GitLab | ✅ Production |
| **Volume mount** | `/workspace/Jenkinsfile` | ⚠️ Tests locaux |
| **Pipeline script** | Copié dans Jenkins UI | ⚠️ Tests rapides |

**Pour votre projet**: Utilisez Git SCM (Méthode 1)

Jenkins accède maintenant sur: **http://localhost:9090**
