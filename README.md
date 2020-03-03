# SYT GEK103x Datamanagement "Distributed Data Structures" (MICT/BORM)

## Aufgabenbeschreibung

### Einführung

Komplexe und aufteilbare Tasks müssen mit Parametern ausgestattet werden und von entsprechenden Koordinatoren gestartet bzw. die erhaltenen Daten wieder zusammengefasst werden. Diese Art von verteilter Programmierung findet in vielen Anwendungsgebieten rege Verwendung (AI Daten Analyse, Lastverteilung, etc.). Hierbei kommt das Prinzip des Master/Worker Patterns (Master-Slave oder Map-Reduce Pattern) zum Einsatz.

### Ziele

Finden Sie eine Lösung, die in einer Cloud-Umgebung ausrollbar (deployable) ist. Die einzelnen Worker sollen unabhängig voneinander bestehen können und mit Input-Parametern gestartet werden. Die berechneten Daten müssen an den Master bzw. als Zwischenberechnung an andere Worker weitergegeben werden können. Die einzelnen Worker sollen unabhängig von der Umgebung gestartet werden können (unterschiedliche Servereinheiten).

### Voraussetzungen

- Grundverständnis von Python oder Java
- Lesen und Umsetzen von APIs
- Fähigkeit komplexe Programmier-Aufgaben zu implementieren und zu verteilen

### Detailierte Ausgabenbeschreibung

Recherchieren Sie mögliche Werkzeuge für das "distributed Computing". Vergleichen Sie mögliche Produkte in Bezug auf folgende Parameter:

- Architektur
- einsetzbare Programmiersprachen
- Datenverteilung und gemeinsamer Speicher
- Performance bei Main-Focus
- Notifikation von Master oder anderen Slaves

Nehmen Sie eine komplexe Aufgabenstellung/Berechnung und zeigen Sie anhand von einer Beispiel-Konfiguration, wie die Verteilung der Berechnung und anschließende Zusammenführung der Daten funktioniert. Bei ähnlichen oder gleichen Berechnungen wäre ein direkter Vergleich (Benchmark) der gewählten Tools/Technologien von Vorteil.

## Bewertung

Gruppengrösse: 1 Person

### Grundlegende Anforderungen **überwiegend erfüllt**

- Vergleich von Technologien

### Grundlegende Anforderungen **zur Gänze erfüllt**

- Einsatz eines Beispiels zur Veranschaulichung der Funktionsweise

### Erweiterte Anforderungen **überwiegend erfüllt**

- Einsatz einer zweiten Implementierung

### Erweiterte Anforderungen **zur Gänze erfüllt**

- Benchmark von zwei verschiedenen Technologien

## Quellen

- "A reactive Java framework for building fault-tolerant distributed systems" Atomix [github](https://github.com/atomix/atomix)
- "What is Atomix?" [atomix.io](https://atomix.io/docs/latest/user-manual/introduction/what-is-atomix/)
- "Introduction to Atomix" [baeldung](https://www.baeldung.com/atomix)
- "Primitive Protocols" [atomix.io](https://atomix.io/docs/latest/user-manual/primitives/primitive-protocols/)
- "The Raft Consensus Algorithm" [online](https://raft.github.io/)
- "In Search of an Understandable Consensus Algorithm" Raft-Paper; Stanford University; [online](https://raft.github.io/raft.pdf)
- "How to Create a distributed Datastore in 10 Minutes" Jonathan Halterman [online](https://jodah.net/create-a-distributed-datastore-in-10-minutes)
- "Spark Quickstart" [online](http://spark.apache.org/docs/latest/quick-start.html)
- "Spark Github repository with examples" [online](https://github.com/apache/spark)
- "Spark Tutorial: Real Time Cluster Computing Framework" [online](https://www.edureka.co/blog/spark-tutorial/)
- "Apache Spark Dockerimage" [online](https://github.com/gettyimages/docker-spark)
- "Open-source software for reliable, scalable, distributed computing" [Apache Hadoop](https://hadoop.apache.org/)
- "High-performance coordination service for distributed applications" [Apache Zookeeper](https://zookeeper.apache.org/doc/current/)
- "Distributed data store" [wikipedia](https://en.wikipedia.org/wiki/Distributed_data_store)
- "Understanding Hadoop v/s Spark v/s Storm" [cognixia.com](https://www.cognixia.com/blog/understanding-hadoop-vs-spark-vs-storm)
- "Comparison Storm and Spark" [whizlabs.com](https://www.whizlabs.com/blog/apache-storm-vs-apache-spark/)
- "etcd - A distributed, reliable key-value store" [online](https://etcd.io/)

---

---

## Vergleich von Technologien

### Atomix

Atomix ist ein Werkzeug um häufige distributed systems Probleme auf verschiedene Arten lösen zu können. Hierbei ist Atomix die Art des Problemes komplett gleichgültig, es stellt einfach `primitives` (Distributed data structures, Distributed communication, Distributed coordination, Group Membership) zur Lösung dieser Probleme zur Verfügung.

#### Architektur

##### Data-Grid

Die häufigste Architektur in Atomix ist ein Simples Data-Grid

![image-20200303134139983](READMEassets/image-20200303134139983.png)

Diese Architektur erlaubt das einfache replizieren von `primitives` auf allen konfigurierten Nodes im Cluster. Dadurch, dass man auch jederzeit Nodes hinzufügen kann und diese dynamisch von den anderen Nodes gefunden werden können, ist diese Architektur flexibel, effizient und skalierbar. Hier ist eine Beispiel Dokumentation aus der Atomix Dokumentation

```python
# The cluster configuration defines how nodes discover and communicate with one another
cluster {
  multicast.enabled: true   # Enable multicast discovery
  discovery.type: multicast # Configure the cluster membership to use multicast
}

# The management group coordinates higher level partition groups and is required
managementGroup {
  type: primary-backup # Use the primary-backup protocol
  partitions: 1        # Use only a single partition for system management
}

# Partition groups are collections of partitions in which primitives are replicated
# This sets up a partition group named `data` on this node
partitionGroups.data {
  type: primary-backup # Use the primary-backup protocol
  partitions: 71       # Use 71 partitions for scalability
  memberGroupStrategy: RACK_AWARE # Replicate partitions across physical racks
}
```

Diese Architektur erlaubt das erstellen von `primitives ` die auf allen Nodes im Cluster repliziert werden. Hier ist eine Beispiel in Java, in dem mittels `MultiPrimaryProtocol` genau das umgesetzt wird.

```java
Map<String, String> map = atomix.mapBuilder("my-map")
  .withProtocol(MultiPrimaryProtocol.builder()
    .withNumBackups(2)
    .build())
  .build();

map.put("foo", "bar");
```

Der größte Nachteil dieser Architektur ist ihre Inkonsistenz in Applikationen die Netzwerkkomponenten verwenden. Weiters kann es zu "split brain" kommen, durch die Tatsache, dass die Knoten nur einfach miteinander vernetzt sind.

##### Consistent Data-Grid

Diese Form des Data-Grid wurde entwickelt und Datenverlust und Inkonsistenz zwischen Netzwerkpartitionen zu vermeiden.

![image-20200303135634071](READMEassets/image-20200303135634071.png)

Atomix verwendet eine komplexe Implementierung des [Raft consensus algorithm](https://raft.github.io/), was kurz gesagt ein Algorithmus ist der für Konsistenz zwischen mehreren Servern sorgt, indem er sie bei Veränderungen zu einem Konsens zwingt den alle eintragen. In Atomix müssen hierfür erstmals einige Nodes in die "Raft management-group" konfiguriert werden.

```python
# The cluster configuration defines how nodes discover and communicate with one another
cluster {
  node {
    id: ${atomix.node.id}   # Should be one of management-group.members
    address: ${atomix.node.address}
  }
  multicast.enabled: true   # Enable multicast discovery
  discovery.type: multicast # Configure the cluster membership to use multicast
}

# The management group coordinates higher level partition groups and is required
# This node configures only a management group and no partition groups since it's
# used only for partition/primitive management
managementGroup {
  type: raft # Use the Raft consensus protocol for system management
  partitions: 1 # Use only a single partition
  members: [raft-1, raft-2, raft-3] # Raft requires a static membership list
}
```

Die Management Group koordiniert den Cluster in Bezug auf Handling von `primitives`, Replikation Konfigurationen, Transaktionen und wählen von primären.

Da wir jetzt die für die Konfiguration notwendigen Nodes haben, brauchen wir noch Nodes die Daten erzeugen und persistiert haben wollen.

```python
# The cluster configuration defines how nodes discover and communicate with one another
cluster {
  node {
    id: ${atomix.node.id}   # Must not be any one of management-group.members
    address: ${atomix.node.address}
  }
  multicast.enabled: true   # Enable multicast discovery
  discovery.type: multicast # Configure the cluster membership to use multicast
}

# This node does not configure a management group since that group is on another
# node. Since the management group is consensus-based, participating in system
# management on this node would constrain its fault tolerance.

# Partition groups are collections of partitions in which primitives are replicated
# This sets up a partition group named `data` on this node
partitionGroups.data {
  type: primary-backup # Use the primary-backup protocol
  partitions: 71       # Use 71 partitions for scalability
  memberGroupStrategy: RACK_AWARE # Replicate partitions across physical racks
}
```

Diese Architektur erlaubt immer noch das simple Erstellen von `primitives ` die auf allen Nodes im Cluster repliziert werden. Es wird nur transparent im Hintergrund das Raft Protocol verwendet. Hier ist eine Beispiel in Java, in dem mittels `MultiPrimaryProtocol` genau das umgesetzt wird.

```java
Map<String, String> map = atomix.mapBuilder("my-map")
  .withProtocol(MultiPrimaryProtocol.builder()
    .withNumBackups(2)
    .build())
  .build();

map.put("foo", "bar");
```

Hier wurden gerade die Konfiguration und Data Nodes getrennt, wobei jede Management Group Node genauso gut Daten auf Sub Sets von Data Nodes replizieren könnte. (siehe obere Architektur)

##### Raft Client-Server



#### Einsetzbare Programmiersprachen

#### Datenverteilung und gemeinsamer Speicher

#### Performance bei Main-Focus

#### Notifikation von Master oder anderen Slaves