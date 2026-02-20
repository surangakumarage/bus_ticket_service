Bus_Ticket_Service

Running Backend Server


Option 1: Tomcat Standalone (Development)
Step 1: Download & Setup Tomcat
# Download Tomcat 10
wget https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.0/bin/apache-tomcat-10.1.0.tar.gz


# Extract
tar -xzf apache-tomcat-10.1.0.tar.gz / or extact zip in windows
cd apache-tomcat-10.1.0


# Set permissions
chmod +x bin/*.sh

Step 2: Set Environment Variables
# Set CATALINA_HOME
export CATALINA_HOME=/path/to/apache-tomcat-10.1.0
export PATH=$CATALINA_HOME/bin:$PATH
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

Step 3: Build & Deploy WAR
# Build server
cd .../bus_ticketer/bus-ticketer-service
mvn clean package -DskipTests


# Copy WAR to Tomcat
cp target/bus-ticketer-service.war $CATALINA_HOME/webapps/bus-ticketer.war


# Start Tomcat
$CATALINA_HOME/bin/startup.sh


# Stop Tomcat
$CATALINA_HOME/bin/shutdown.sh

Tomcat Configuration (setenv.sh / setenv.bat)
Create $CATALINA_HOME/bin/setenv.sh:
#!/bin/bash


# Memory settings
export CATALINA_OPTS="-Xms256m -Xmx512m -XX:+UseStringDeduplication"


# Java version
export JAVA_HOME=$(/usr/libexec/java_home -v 21)


# UTF-8 encoding
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"


# Debugging (optional)
# export CATALINA_OPTS="$CATALINA_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"


Option 2: Docker (Recommended for Production)
# Navigate to project directory
cd .../bus_ticket_service


# Set Java home
export JAVA_HOME=$(/usr/libexec/java_home -v 21)


# Build WAR
cd .../bus-ticket-service
mvn clean package -DskipTests


# Start Tomcat container
docker-compose -f docker-compose.yml up -d


# Verify server is running
curl http://localhost:9090/bus-ticket-service/health


# View logs
docker-compose -f docker-compose-tomcat.yml logs -f


# Stop server
docker-compose -f docker-compose-tomcat.yml down


Available Endpoints
GET http://localhost:9090/bus-ticketer-service/health — Health check
GET http://localhost:9090/bus-ticketer-service/info — Service information
GET/POST http://localhost:9090/bus-ticketer-service/api/v1/reservation/availability — Check seat availability
POST http://localhost:9090/bus-ticketer-service/api/v1/reservation/book — Book a ticket
