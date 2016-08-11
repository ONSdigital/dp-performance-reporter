FROM onsdigital/java-component

WORKDIR /usr/src

ADD ./target/dependency ./target/dependency
ADD ./target/classes ./target/classes

ENTRYPOINT java -cp "./target/dependency/*:./target/classes/" com.onsdigital.performance.reporter.Main
