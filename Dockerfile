FROM openjdk:21
WORKDIR /app

# Copy the JAR files for both the proxy client and server
COPY proxy-server/target/proxy-server-1.0.0.jar /app/proxyserver.jar
COPY proxy-client/target/proxy-client-1.0.0.jar /app/proxyclient.jar


# Expose ports
EXPOSE 8080
EXPOSE 9090

# Run both the client and server
CMD ["sh", "-c", "java -jar /app/proxyser  ver.jar & java -jar /app/proxyclient.jar"]