# Tutoriel succinct pour l'installation et l'utilisation de Jade

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Tutoriel succinct pour l'installation et l'utilisation de Jade]
	- [Introduction](#introduction)
	- [Installation](#installation)
	- [Configuration de ECLIPSE](#configuration-de-eclipse)
	- [Création du premier agent]
		- [Code](#code)
		- [Commentaires](#commentaires)
	- [Créer plusieurs agents programmatiquement]
	- [Les messages](#les-messages)
		- [Création et envoie de message]
		- [Réception et réponse au message]
		- [Recherche de destinataire](#recherche-de-destinataire)
		- [Méthode 1 : AMS]
		- [Méthode 2 : DF]
			- [Enregistrement dans l'annuaire]
			- [Lecture de l'annuaire]
	- [Les Behaviours dans jade]
- [Autres documents](#autres-documents)

<!-- /TOC -->

## Introduction
Voir le [Site officiel](http://jade.tilab.com/doc/tutorials/JADEAdmin/jadeArchitecture.html) pour plus d'info...

>JADE (Java Agent DEvelopment Framework) is a software framework fully implemented in Java language. It simplifies the implementation of multi-agent systems through a middle-ware that claims to comply with the FIPA specifications and through a set of tools that supports the debugging and deployment phase. The agent platform can be distributed across machines (which not even need to share the same OS) and the configuration can be controlled via a remote GUI. The configuration can be even changed at run-time by creating new agents and moving agents from one machine to another one, as and when required. The only system requirement is the Java Run Time version 5 or later.

>The communication architecture offers flexible and efficient messaging, where JADE creates and manages a queue of incoming ACL messages, private to each agent. Agents can access their queue via a combination of several modes: blocking, polling, timeout and pattern matching based. The full FIPA communication model has been implemented and its components have been clearly distincted and fully integrated: interaction protocols, envelope, ACL, content languages, encoding schemes, ontologies and, finally, transport protocols. The transport mechanism, in particular, is like a chameleon because it adapts to each situation, by transparently choosing the best available protocol. Most of the interaction protocols defined by FIPA are already available and can be instantiated after defining the application-dependent behaviour of each state of the protocol. SL and agent management ontology have been implemented already, as well as the support for user-defined content languages and ontologies that can be implemented, registered with agents, and automatically used by the framework.

>JADE is being used by a number of companies and academic groups, such as BT, Telefonica, CNET, NHK, Imperial College, IRST, KPN, University of Helsinky, INRIA, ATOS, and many others.

>JADE is distributed in Open Source under the LGPL License. Further details and documentation can be found at http://jade.tilab.com/.


## Installation
- Téléchargez JadeAll sur : [JadeAll](http://jade.tilab.com/download/jade/)
- Décompressez le fichier principal et tous les sous-fichiers dans  `c:\jade`
- Créez une variable d'environnement **classpath** et ajoutez les quatre fichiers `jade.jar`

```shell
c:\jade\lib\jade.jar
```
- Pour tester l'exécution, faire `java jade.Boot -gui` (faire attention au B majuscule)

Nous avons ici un *main-container* avec 3 agents par défaut :
1. **ams** : *Agent Management System* fournit le service de nommage et qui représente l'autorité de la plateforme.
2. **df** : *Directory Facilitator* fournit un système d'annuaire qui permet aux agents de retrouver les agents fournisseurs de services.
3. **rma** : *Remote Management Agent* qui est l'agent en charge de l'affichage graphique de la console (qu'on a obtenu avec l'option `-gui`.

## Configuration de ECLIPSE
- Vérifiez la présence de JAVA en ouvrant une console et tapant les commandes suivantes :
  - `java  -version` : Cela vous indique que vous disposez au moins du JRE et que vous êtes donc en mesure d’exécuter des programmes écrits en JAVA.
  - `javac -version` : Cela vous indique que vous disposez bien du JDK et vous allez pouvoir compiler vos codes sources pour créer des programme JAVA.

- Configurez ECLIPSE : Allez dans `windows > preferences`
  - Tapez `encoding` dans le champ de recherche et dans *workspace* mettez **UTF-8** dans *Text file encoding*
  - Tapez `deprecated` dans le champ de recherche et dans *Errors/Warnings* cochez l'option *Signal overriding or implementing deprecated method*.


## Création du premier agent
- Ouvrez ECLIPSE et créez un projet
- Dans le déroulé du projet, créez le package `src > Tuto` et créez un fichier `MyAgent.java`

### Code
```java
package Tuto;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class MyAgent extends Agent {

 protected void setup() {
  System.out.println("Je suis le tout premier Agent : " +
    getLocalName() + " appelé aussi" + getAID().getName());

  addBehaviour(new TickerBehaviour(this, 1000) {
   @Override
   protected void onTick() {
    System.out.println("Blink");
   }
  });

 }
}

```

- Cliquez avec le bouton droit sur le projet puis aller dans `Build Path > Configure Build Path...`
- Appuyez sur `Add external JARs` et choisissez le fichier jade.jar (`c:\jade\lib\jade.jar`)
- Aller dans `Run > Run Configuration`
  - Double-cliquez sur `java application`
  - Dans `Main Class` tapez **jade.Boot**
  - Dans `Program arguments` tapez **-gui Testeur:Tuto.MyAgent**
  - Cliquez sur `Apply` puis `run`
> Pensez à fermer les autres instances pour éviter d'avoir l'erreur **No ICP active**

### Commentaires
- **getLocalName()** permet d'obtenir le nom de l'agent en local
- **getAID().getName()** permet d'obtenir le couple `<nickname>@<platform-name>`
- **addBehaviour** permet de créer un comportement à l'agent. Ici, il s'agit d'un évènement qui se passe toutes les sec. Il y a plusieurs types de Comportements :
  - **OneShotBehaviour**
  - **CyclicBehaviour**
  - **Behaviour **
  - **WakerBehaviour**
  - **TickerBehaviour**

De base, l'agent fera son `action()` tant que `done()` n'est pas `true`, sous la forme de base suivante :

```java
public class MyBehaviour extends Behaviour {
 private Boolean finished = false;

 public void action() {
  while (true) {
   // faire quelque chose qui modifie finished
  }
 }

 public boolean done() {
  return finished;
 }
}
```
## Créer plusieurs agents programmatiquement

Dans ce mode, il n'est plus nécessaire de faire la configuration du `run` comme précédemment... Il suffit de créer le main suivant (dans un autre package par exemple) et d'exécuter en en tant que programme java :

```java
package main;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.*;

public class Gysomate {

 public static void main(String[] args) {
  Runtime runtime = Runtime.instance();
  Profile profile = new ProfileImpl();
  profile.setParameter(Profile.MAIN_HOST, "localhost");
  profile.setParameter(Profile.GUI, "true");
  ContainerController containerController = runtime.createMainContainer(profile);

  AgentController agentController01,agentController02;

  try {
   agentController01 = containerController.createNewAgent("Agent01", "Tuto.MyAgent", null);
   agentController01.start();
   agentController02 = containerController.createNewAgent("Agent02", "Tuto.MyAgent", null);
   agentController02.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


 }

}

```
> Un exemple de code fonctionnel (eclipse) se trouve sur [mon git](https://github.com/gyassine/JadeTuto)...
> Ex 1, Agent MyAgent

## Les messages

### Création et envoie de message
De base, pour envoyer un message il suffit de faire :
```java
  ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
  msg.setContent("Ping");
  msg.addReceiver(new AID("Receiver", AID.ISLOCALNAME));
  send(msg);
```
Remarques :
- Dans la ligne : `msg.addReceiver(new AID("Receiver", AID.ISLOCALNAME));`, il faut identifier qui est le destinataire du message...
- `send()` est une méthode de `void jade.core.Agent.send`

### Réception et réponse au message
La réception se fait grâce à la méthode `receive()`. Et la réponse peut se faire simplement en utilisant la méthode `getSender()` du message :
```java
ACLMessage msg = receive();
if (msg != null) {
  System.out.println(" - " + myAgent.getLocalName() + " <- " + msg.getContent());

  ACLMessage reply = msg.createReply();
  reply.setPerformative(ACLMessage.INFORM);
  reply.setContent(" Pong");
  send(reply);
}
block();
```
Remarques :
- L'instruction `block()` *n'arrête pas l'exécution*, il *programme juste la prochaine exécution*. Si vous ne mettez pas de `block ()`, le comportement restera actif et provoquera une boucle.
- Il est important de tester `if (msg != null)` pour éviter les *`Null Pointer Exception`*

> Un exemple de code fonctionnel (eclipse) se trouve sur [mon git](https://github.com/gyassine/JadeTuto)...
> Ex 2, Agents Sender et Receiver

### Recherche de destinataire

Dans le code précédent, nous avions un destinataire connu. Il est possible aussi d'envoyer un message à des agents dont on ne connait pas le nom à l'avance.

### Méthode 1 : AMS
**AMS** est l'*Agent Management System* qui fournit le service de nommage et représente l'autorité de la plateforme. Il garde la trace de tous les agents présents.

```java
package Tuto;

import jade.core.Agent;
import jade.core.AID;

import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.*;

public class AMSLister extends Agent {
 protected void setup() {
  AMSAgentDescription[] agents = null;
  try {
   SearchConstraints c = new SearchConstraints();
   c.setMaxResults(new Long(-1));
   agents = AMSService.search(this, new AMSAgentDescription(), c);
  } catch (Exception e) {
   System.out.println("Erreur " + e);
   e.printStackTrace();
  }

  System.out.println("----- Agent AMS Lister ----- ");
  for (int i = 0; i < agents.length; i++) {
   AID agentID = agents[i].getName();
   System.out.print("    " + i + ": " + agentID.getName());
   if (agentID.equals(getAID()))
    System.out.print(" <----- C'est moi ");
   System.out.println("");
  }
  System.out.println("---------------------------- ");

 }

}
```

> Un exemple de code fonctionnel (eclipse) se trouve sur [mon git](https://github.com/gyassine/JadeTuto)...
> Ex 3, Agent AMSLister

### Méthode 2 : DF
**DF** est le *Directory Facilitator* qui fournit un système de pages jaunes qui permet aux agents de retrouver les agents fournisseurs de services.

#### Enregistrement dans l'annuaire
L'agent s'enregistre en fournissant son "type" au sein de la méthode `setup()`.

```java
 ServiceDescription sd = new ServiceDescription();
 sd.setType("Service1");
 sd.setName(getLocalName());
 register(sd);
```

La méthode `register()` est la suivante :
```java
 void register(ServiceDescription sd) {
  DFAgentDescription dfd = new DFAgentDescription();
  dfd.setName(getAID());
  dfd.addServices(sd);

  try {
   DFService.register(this, dfd);
  } catch (FIPAException fe) {
   fe.printStackTrace();
  }
 }
```

#### Lecture de l'annuaire
Un autre agent peut donc accéder à l'annuaire et obtenir toutes les personnes inscrites en faisant :
```java
   DFAgentDescription dfd = new DFAgentDescription();
   DFAgentDescription[] result = DFService.search(this, dfd);

   System.out.println("Présent ds l'annuaire : " + result.length + " éléments");
   for (int i = 0; i < result.length; i++)
    System.out.println(" " + result[i].getName());
```

Ou obtenir les agents fournissant un service particulier :
```java
   DFAgentDescription dfd = new DFAgentDescription();
   ServiceDescription sd = new ServiceDescription();

   sd.setType("Service1");
   dfd.addServices(sd);
   DFAgentDescription[] result = DFService.search(this, dfd);

   System.out.println("Fournisseur de Service 1 : " + result.length + " éléments");
   for (int i = 0; i < result.length; i++)
    System.out.println(" " + result[i].getName().getLocalName());
```

> Un exemple de code fonctionnel (eclipse) se trouve sur [mon git](https://github.com/gyassine/JadeTuto)...
> Ex 4, Agents DFLister et AgentFS

Remarques :
- Sur le résultat, `result[i].getName()` fournit en fait l'`AID` alors que `result[i].getName().getLocalName()` fournit le **LocalName**
- Un agent doit se désinscrire de l'annuaire avant de s'éteindre. Sinon, son nom restera présent dans l'annuaire.
- Il est possible de s'inscrire à une notification auprès de l'annuaire pour qu'il nous signale lorsqu'un agent d'un type particulier s'enregistre en envoyant le message `DFService.createSubscriptionMessage(...)`
- Dans le code, nous avons utilisé des **arguments externes** pour créer l'agent :
```java
Object [] argsAgent1= new Object[1];
argsAgent1 [0] = "Service1";
//......
ac04 = containerController.createNewAgent("AgentFS1", "Tuto.AgentFS",argsAgent1);
```
- ...que nous avons ensuite récupéré en faisant :
```java
getArguments()[0].toString()
```
## Les Behaviours dans jade

Il y a deux types de Behaviours :
- *Les primitives* :
  - **SimpleBehaviour** : comportement de base. Il n'implémente pas la méthode `done()` et laisse son implémentation au programmeur, donc il peut planifier la terminaison de son Behaviour selon ses besoins.

  - **CyclicBehaviour** : ce comportement reste actif tant que son agent est actif et sera appelé à plusieurs reprises après chaque évènement. Très utile pour gérer la réception des messages. La classe CyclicBehaviour implémente la méthode `done()` qui retourne toujours `false`.
    - **TickerBehaviour** : comportement cyclique qui s'exécute périodiquement, par la méthode `onTick()`.
  - **OneShotBehaviour** : comportement qui s'exécute une et une seule fois puis il se termine. La classe OneShotBehaviour implémente la méthode `done()` et elle retourne toujours `true`.
    - **WakerBehaviour** : comportement qui exécute du code utilisateur une fois à une heure précise, par la méthode `onWake()` après une période (en ms) passée comme argument au constructeur
    - **ReceiverBehaviour** : comportement qui se déclenche lorsqu'un type de message donné est reçu ou lors d'un timeout.
- *Les composites* : qui peuvent combiner des comportements simples et composites pour exécuter en séquence ou en parallèle
  - **ParallelBehaviour** : contrôle un ensemble de comportements "enfants" qui s'exécutent en parallèle. Le groupe se termine lorsque TOUS les "enfants" ont été exécutés, N "enfants" sont exécutés ou un "enfant" est exécuté.
  - **SequentialBehaviour** : ce comportement exécute ses comportements "enfants" l'un après l'autre (sa méthode `done()` retourne `true`) et se termine lorsque le dernier "enfant" est terminé. Les sous-Behaviours sont ajoutés au sequentielBehaviour par la méthode `addSubBehaviour()`. L'ordre de l'ajout détermine l'ordre d'exécution.

  - **FSMBehaviour** : ce comportement implémente un automate à états finis dont chaque état correspond à l'exécution d'un comportement "enfant".
  - **SimpleAchieveREInitiator** et **SimpleAchieveREResponder** : sont deux comportements créés pour les *"FIPA-Request-like interaction protocols"* (voir le  [guide](https://www.iro.umontreal.ca/~dift6802/jade/doc/programmersguide.pdf) en page 35)

Un bon [site](https://djug.developpez.com/java/jade/behaviours/) qui résume la majorité de ces behaviours en français

# Autres documents

- [Intro en fr](https://www.emse.fr/~boissier/enseignement/maop11/courses/jade-prog-4pp.pdf)
- [Tuto en Fr](https://djug.developpez.com/)
