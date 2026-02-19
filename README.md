# bus_ticket_service
bus_ticket_service


### Running Backend Server

#### Option 1: Docker (Recommended for Production)

```bash
# Navigate to project directory
cd .../bus_ticketer

# Start Tomcat container
docker-compose -f docker-compose-tomcat.yml up -d

# Verify server is running
curl http://localhost:9091/bus-ticketer/health

# View logs
docker-compose -f docker-compose-tomcat.yml logs -f

# Stop server
docker-compose -f docker-compose-tomcat.yml down




#### Option 2: Tomcat Standalone (Development)

**Step 1: Download & Setup Tomcat**
```bash
# Download Tomcat 10
wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.0/bin/apache-tomcat-10.1.0.tar.gz

# Extract
tar -xzf apache-tomcat-10.1.0.tar.gz
cd apache-tomcat-10.1.0

# Set permissions
chmod +x bin/*.sh
```

**Step 2: Set Environment Variables**
```bash
# Set CATALINA_HOME
export CATALINA_HOME=/path/to/apache-tomcat-10.1.0
export PATH=$CATALINA_HOME/bin:$PATH
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Step 3: Build & Deploy WAR**
```bash
# Build server
cd .../bus_ticketer/bus-ticketer-service/server
mvn clean package -DskipTests

# Copy WAR to Tomcat
cp target/bus-ticketer-service-1.0.0.war $CATALINA_HOME/webapps/bus-ticketer.war

# Start Tomcat
$CATALINA_HOME/bin/startup.sh

# View logs
tail -f $CATALINA_HOME/logs/catalina.out
```

**Step 4: Verify Server**
```bash
# Test health endpoint
curl http://localhost:8080/bus-ticketer/health

# Test availability endpoint
curl "http://localhost:8080/bus-ticketer/api/v1/reservation/availability?origin=A&destination=B&passenger_count=1&journey_date=2026-02-18"

# Stop Tomcat
$CATALINA_HOME/bin/shutdown.sh
```

#### Tomcat Configuration (`catalina.properties` or `setenv.sh`)

Create `$CATALINA_HOME/bin/setenv.sh`:
```bash
#!/bin/bash

# Memory settings
export CATALINA_OPTS="-Xms256m -Xmx512m -XX:+UseStringDeduplication"

# Java version
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# UTF-8 encoding
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# Debugging (optional)
# export CATALINA_OPTS="$CATALINA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
```