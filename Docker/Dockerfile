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

RUN mkdir /root/arx-test
WORKDIR /root

WORKDIR /root/arx-test
RUN git clone https://github.com/arx-deidentifier/arx.git
WORKDIR /root/arx-test/arx
RUN ant

WORKDIR /root/arx-test
RUN git clone --branch pcc-resubmission https://github.com/eicherj/subframe.git
WORKDIR /root/arx-test/subframe
RUN ant

ADD resources/utility		/root/arx-test/utility
WORKDIR /root/arx-test/utility
RUN ant

ENV REBUILD 6
ENV ANT_HOME /usr/share/ant

WORKDIR /
WORKDIR /root/arx-test
RUN git clone --branch pcc              https://github.com/ElMuto/anonbench.git
ADD resources/hamcrest-core-1.3.jar 	${ANT_HOME}/lib/
ADD resources/junit-4.12.jar 		${ANT_HOME}/lib/
ADD resources/data/*    	/root/arx-test/anonbench/data/
ADD resources/hierarchies/*	/root/arx-test/anonbench/hierarchies/
WORKDIR /root/arx-test/anonbench
#RUN ant -f buildInDocker.xml

RUN echo "alias ll='ls -l'" >> /root/.bashrc

ENTRYPOINT ["top", "-b"]
CMD ["-c"]
