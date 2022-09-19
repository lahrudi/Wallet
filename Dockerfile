FROM openjdk:17
MAINTAINER AlirezaGholamzadehLahrudi@gmail.com
COPY target/leovegas-wallet-0.0.1-SNAPSHOT.jar leovegas-wallet.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/leovegas-wallet.jar"]


