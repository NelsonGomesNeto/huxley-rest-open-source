FROM marcioaug/grails

MAINTAINER Marcio Augusto Guimarães "marcioaugustosg@gmail.com"

RUN useradd -d /home/huxley -m -s /bin/bash -G sudo huxley

USER huxley
WORKDIR /home/huxley/huxley-rest

VOLUME /home/huxley/huxley-rest
EXPOSE 8080

#docker build -t="marcioaug/huxley-rest" .
#docker run -i -t --name rest -p 8080:8080 -v $PWD:/home/huxley/huxley-dev marcioaug/huxley-rest
#docker run -it -v /home/marcio/Projects/marcioaug/misc/:/opt/misc/ --link huxleyrest_mysql_1:mysql --rm mysql sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD" huxley-dev < /opt/misc/huxley-dev.sql'
