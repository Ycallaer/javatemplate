# Java project template

## Introduction
The goal of this repo is to have a starting template for each java project.
This allows for faster development time and cut down on the copy paste activities.

## Content of the template repo (in order of folder / file structure)

### .mvn : Takari wrapper
As maven is not always installed we provide maven within the project by installing it through the Takari wrapper

### aks: Kubernetes
A simple namespace configuration and deployment has been foreseen. Please check the files in the folder and do not forget to replace the following values:

* PROJECT_NAMESPACE : The namespace in which the configuration will run on AKS
* PROJECTNAME: The name of the project
* PROJECTCONTAINERREGISTRY: Registry on which you put your docker file

### src: ProjectMetrics
I have added a small implementation of the dropwizard metric package which will allow you to create metrics in your package.
The example show the metric of all messages going through the application and it will report every 10 seconds on those metrics.


### Dockerfile
In the docker file we start by using openjdk to avoid having to install all java dependencies.
Furthermore a couple of tools are installed and lastly logstash is configured. If you don't want to use it you can remove this.

### Jenkinsfile
As we do not manual configure jobs in jenkins I provided a pipeline script which does the following actions:

* Checkout the code
* Decrypt the config file: We keep config files encrypted with git crypt. We have a jenkins.pgp which can decrypt the files during the jenkins build
* Run unit test and create jacoco report
* Start a sonarqube build
* Build the jar and upload to nexus
* Build container
* Push to registry
* Deploy AKS

Please note that in the environment section you will need to change the parameters

### Makefile
We have provided a makefile which you can extend if needed

### Pom : maven
This is where all your dependencies go. Currently it is configured for the following dependencies:

* log4j
* junit
* dropwizards

To create the reports we use jacoco. The standalone jar is build with dependencies against java 1.8

### settings and settings-security
These files are needed to upload the JAR to nexus. The credentials should be given by your nexus administrator.

### Sonar project
This file is configured to interact with sonarqube. Please note that you need to edit this file.
Currently it has also been configured to upload the jacoco reports to sonarqube

## Wrap up
While it is true there are a dozen of tools out there to build and deploy I tried to take the tools which are most commonly used.
You might or might not agree with that, the goal is to give a basic template which can be customised by the developer in question.