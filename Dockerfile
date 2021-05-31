FROM openjdk:8u232

ARG SBT_VERSION=1.4.9

# Install sbt
RUN \
  mkdir /workdir/ && \
  cd /workdir/ && \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  cd && \
  rm -r /workdir/ && \
  sbt sbtVersion

RUN mkdir -p /root/json_parser/project
ADD build.sbt /root/json_parser/
ADD ./project/plugins.sbt /root/json_parser/project
ADD ./src /root/json_parser/src
RUN cd /root/json_parser && sbt clean compile test assembly
WORKDIR /root/json_parser
ENTRYPOINT ["java","-jar","/root/json_parser/target/scala-2.12/JsonParser-assembly-0.1.jar"]

