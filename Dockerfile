FROM maven:3.9.6

WORKDIR /build

COPY . /build

RUN mvn clean install -Pcontainer -DskipTests

COPY target/*.jar /build/project.jar

CMD ["java", "-jar", "/build/project.jar"]