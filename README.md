# Multi-tile Architecture Simulator

This repository presents the implementation of a system level simulator.
Here, the simulation of an application to a many core architecture is performed.
The simulator takes as inputs the target architecture, the application and the scheduler.


## Application
The application is defined as a dataflow graph. A dataflow graph consists of a collection of actors that are connected via communication channels.
In the context of this repository, the communication channels are implemented as First-In-First-Out (FIFO) buffers.
Actors model the functionality of the application under observation.
E.g., the following figure presents the dataflow of an implementation of the sobel algorithm.

![Application Graph](img/DFG.png)

To define a application graph, you can declare an actor as:

```ruby
require 'redcarpet'
markdown = Redcarpet.new("Hello World!")
puts markdown.to_html
```



## Target Architecture

![Target Architecture overview](img/targetArch.png)

## Scheduler
