# Smarter Factory

This repository contains the implementation of the augmented (retrofitted) production and storage processes
for the Fischertechnik Learning Factory Industry 4.0 (9V).

Here you can find 5 Java-based microservices implemented using Spring Boot to extend the existing
processes in the learning factory with additional functionality. The *Camunda* microservice contains
an embedded business process management system, namely Camunda Platform 7 (Community Edition), that
manages, executes, and monitors the business processes.

## Instructions ##

### Hardware Setup ###
We assume the standard, out-of-the-box setup of the Fischertechnik Learning Factory Industry 4.0 (9V) to
be used, with all 5 TXT controllers connected to a local WiFi network with IP configurations according
to [this documentation](https://github.com/fischertechnik/txt_training_factory). The computer used to run
the software provided in this repository also needs to be connected to this network. Make sure that
this computer is able to reach the smart factory's MQTT broker 
(per default, running at: 192.168.0.10:1883, user: txt, pwd: xtx).

Note that you can adjust parameters, including the MQTT broker configurations in the *application.properties*
files of the individual microservices.

### Software Setup ###

The implementation uses Maven as build system and Docker as runtime platform. Java 17 is used. All microservices
are standalone Spring Boot applications.

First, all microservices need to be built by running:
```bash
mvn clean install
```

Once this process finished successfully. The Docker containers have to be built and started:
```bash
docker compose up --build
```
All 5 microservices should be started in Docker containers afterward and the microservices should be
connected to the factory's MQTT broker. The user interface of the BPMS will be available at:
http://127.0.0.1:8080 (user: demo / pwd: demo) of the computer running this software project. 

The two augmented
business processes will be deployed automatically upon start. They can be found in folder *smarter-factory/camunda/src/main/resources/*
and modified using [Camunda Modeler](https://camunda.com/download/modeler/). New business processes 
can be modeled and uploaded to the BPMS using Camunda Modeler ([see instructions](https://docs.camunda.org/get-started/quick-start/deploy/)).

#### Simulation ####

Note that if there is no physical learning factory available. You can also simulate the execution of the
original storage and production processes in the smart factory using a publicly available MQTT broker:
The *data* folder in this repository contains exemplary data recorded from the original process
execution in the smart factory via its MQTT interface. It can be replayed line by line, by sending
these JSON messages, line by line, to the MQTT topics specified in the first
attribute of each line as key. The actual payload of the message is the associated value. By paying attention to the 
embedded timestamps, the real-world execution can be simulated. The MQTT configuration
of all microservices need to be adjusted according to the MQTT broker used (see *application.properties* files).