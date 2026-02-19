# Dockerfile for Bus Ticketer Service
# Pure in-memory storage - No database required
# Uses Tomcat to serve the WAR file

FROM tomcat:10-jdk21

# Set working directory
WORKDIR /usr/local/tomcat

# Remove default webapps
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the built WAR file to webapps as ROOT
COPY target/bus_ticket_service-1.0.0.war /usr/local/tomcat/webapps/ROOT.war

# Update Tomcat server.xml to listen on port 9091
RUN sed -i 's/port="8080"/port="9091"/g' /usr/local/tomcat/conf/server.xml

# Expose port
EXPOSE 9091

# Environment variables
ENV CATALINA_OPTS="-Xms256m -Xmx512m -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:9091/bus-ticketer/ || exit 1

# Start Tomcat
CMD ["catalina.sh", "run"]
