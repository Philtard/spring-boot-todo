FROM java
MAINTAINER Philipp Seeger <philipp.seeger@nttdata.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/todo/todo.jar"]
# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib /usr/share/todo/lib
# Add the service itself
ADD target/todo.jar /usr/share/todo/todo.jar
