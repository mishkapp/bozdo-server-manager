FROM babashka/babashka:alpine

ADD ./script/start.clj /start

RUN apk add openjdk8-jre

RUN mkdir "server"

RUN wget https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar -O /server/packwiz-installer-bootstrap.jar

RUN chmod +x /start

CMD ["/bin/bb", "start"]