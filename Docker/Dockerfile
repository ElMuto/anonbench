FROM oracle-java8

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

ENV BASEDIR /root/arx-test
RUN mkdir ${BASEDIR}

ENV ANT_HOME /usr/share/ant
ENV CODE_ADCC ${BASEDIR}/anonbench

WORKDIR /
WORKDIR ${BASEDIR}
RUN git clone --branch pcc              https://github.com/ElMuto/anonbench.git
ADD resources/data/*    	/root/arx-test/anonbench/data/

ADD resources/hierarchies/*	/root/arx-test/anonbench/hierarchies/
WORKDIR ${CODE_ADCC}
#RUN ant

RUN echo "alias ll='ls -l'" >> /root/.bashrc

ENTRYPOINT ["top", "-b"]
CMD ["-c"]
