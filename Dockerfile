FROM maven:latest

WORKDIR /build

COPY . /build

RUN mvn clean install -Pcontainer -DskipTests

COPY target/*.jar /build/project.jar

CMD ["java", "-jar", "/build/project.jar"]