# Visualization of Publisher-Subscriber Architectural pattern

This project is a robust HTTP server implementation designed to handle and process HTTP requests using a multi-threading.

the server include request parsing for extracting relevant information, a servlet system used to handle different http requests 
such as GET. and POST and implements  creates real-time graph visualization using [ECharts](https://echarts.apache.org/en/index.html).


## Background
- A  **Publisher-Subscriber** architecture pattern enables us to construct a computational graph capable of executing complex calculations.
- The nodes of the graph are split into two groups:
  - **Agent** : A node that receives messages from some nodes, performs a calculation and outputs (publishes) the result to some other nodes.
  - **Topic** : A node that sends a message to an agent, and receives messages from agents.
  - *Note that an **agent** can only be connected to **topics**, and a **topic**  can only be connected  to an **agent.***

## Navigation
* [Setup](#Setup)
* [Installation](#installation)
* [Configuration](#Configuration)
* [Running](#Running)
* [Usage](#Usage)
* [ProjectDirectories](#ProjectDirectories)

## Setup
### Prerequisites
Before you begin, ensure you have met the following requirements:
- Java Development Kit (JDK) 8 or higher installed on your machine.
- An IDE.
- A modern web browser for accessing the visualization interface.


### Installation
- Clone the repository
```
git clone https://github.com/daniel1407y/AdvancedProgrammingProject.git
```
- Open the repository using your preferred IDE.

### Configuration
In order to create a graph there is a need for conf file.
- Have a `.conf` extension.
- End with an empty line.
- be created in the following format:
```
//Config.AgentClassName
//Agent subscribers (Topic)
//Agent publishers (Topic)
```

For example,
```
Config.PlusAgent
A,B
C
Config.IncAgent
C
D

```

*In our repository there are a few already defined agents and configurations, but you are encouraged to create your own!*


## Running
- After uploading the navigation file, navigate to the folder `/src/server`.
- Run `MainTrain.Java` to run your computer as a local host.
- Open your preferred web browser and navigate to `http://localhost:8080/app/index.html` to visualize the architecture created using the configuration file.

##  Usage
- Upload your configuration file on the left side of the screen and click on `Deploy`.
- After the graph has been displayed in the middle of the screen, Write a topic name and a message in the provided fields to publish a message from the topic.
- On the right side  of the screen are the topics of the provided configuration and the messages that they last received.


## ProjectDirectories
```bash
├───.idea
├───config_files
├───created_directories
├───html_files
└───src
    ├───Config
    ├───graph
    ├───server
    ├───servlets
    └───views
```

[![My Skills](https://skillicons.dev/icons?i=java,js,html,css)](https://skillicons.dev)
