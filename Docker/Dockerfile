FROM openjdk:8

RUN apt-get update && apt-get install -y --no-install-recommends \
		subversion \
		git \
		ant \
		ant-optional \
		vim \
	&& rm -rf /var/lib/apt/lists/*

# Set the timezone.
RUN mv /etc/localtime /etc/localtime.bak && \
    ln -s /usr/share/zoneinfo/Europe/Berlin /etc/localtime

ENV BASEDIR /root/test
RUN mkdir ${BASEDIR}

ENV ANT_HOME /usr/share/ant

ENV GIT_BRANCH			pcc
ENV GIT_URL				https://github.com/ElMuto/anonbench.git
ENV PROJECT_NAME	 	adcc
ENV CUSTOM_TEST_CLASS	"**/Shortrunners.java"
ENV ANT_TARGET			custom-test

WORKDIR ${BASEDIR}

ADD resources/data/*    	/root/test/data/
ADD resources/hierarchies/*	/root/test/hierarchies/

WORKDIR ${BASEDIR}
ADD startup.sh ${BASEDIR}/
RUN chmod 700 ${BASEDIR}/startup.sh

CMD ${BASEDIR}/startup.sh
