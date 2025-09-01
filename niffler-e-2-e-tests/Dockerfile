FROM eclipse-temurin:21-jdk

WORKDIR /niffler-ng-8
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./niffler-e-2-e-tests ./niffler-e-2-e-tests
COPY ./niffler-grpc-common ./niffler-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

RUN chmod +x ./gradlew
CMD ./gradlew test -Dtest.env=docker -Dclient.impl=api -Dbrowser=${BROWSER} -Duser.timezone=Europe/Moscow