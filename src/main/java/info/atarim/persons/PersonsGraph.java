package info.atarim.persons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonsGraph {
	private static final Logger LOG = LoggerFactory.getLogger(PersonsGraph.class );
	
	private Map<Address, Set<Person>> personsByAddress = new HashMap<>();
	private Map<Name, Set<Person>> personsByName = new HashMap<>();
	private Map<Person, Set<Person>> personsGraph= new HashMap<>();
	

	public static void main(String[] args) {
		Address address1 = new Address("Bletchey Park", "Cambridge");
    	Address address2 = new Address("Picadili", "London");
    	Address address3 = new Address("5th Av", "New York");
    	
    	Name fullName1 = new Name("Alan", "Turing");
    	Name fullName2 = new Name("Joan", "Clarke");
    	Name fullName3 = new Name("Grace", "Hopper");
    	
    	Person person11 = new Person(fullName1, address1);
    	Person person12 = new Person(fullName1, address2);
    	Person person21 = new Person(fullName2, address1);
    	Person person22 = new Person(fullName2, address2);
    	Person person31 = new Person(fullName3, address3);
    	
    	List<Person> people = new ArrayList<>();
    	people.add(person11);
    	people.add(person12);
    	people.add(person21);
    	people.add(person22);
    	people.add(person31);
    	
    	
    	PersonsGraph personsGraph = new PersonsGraph();
    	
    	Person[] personsArray = new Person[5];
    	
    	personsGraph.init(people.toArray(personsArray));
    	LOG.info(personsGraph.toString());
    	
    	printRelationashipLevel(personsGraph, person11, person11);
    	printRelationashipLevel(personsGraph, person11, person12);
    	
    	printRelationashipLevel(personsGraph, person11, person21);
    	printRelationashipLevel(personsGraph, person11, person22);
    	
    	printRelationashipLevel(personsGraph, person11, person31);
    	
    	
    	printRelationashipLevel(personsGraph, person21, person31);
    	
	}


	public static void printRelationashipLevel(PersonsGraph personsGraph, Person personA, Person personB) {
		int level = personsGraph.findMinRelationLevel(personA, personB);
    	LOG.info("Relatinon between " + personA + " to " + personB + " is " + level);
	}

	
	public void init(Person[] people) {
		// Assumption there can't 2 people with the same name and address
		for (int i = 0; i < people.length; i++) {
			
			Person person = people[i];
			
			personsGraph.put(person, new HashSet<>());
			
			Address address = person.getAddress();
			Set<Person> relativesByAddressSet = personsByAddress.get(address);
			if (relativesByAddressSet == null) {
				relativesByAddressSet = new HashSet<>();
				personsByAddress.put(address, relativesByAddressSet);
			}
			relativesByAddressSet.add(person);
			addTofirstLevelRelativePeople(person, relativesByAddressSet);
			
			
			Name fullName = person.getFullName();
			Set<Person> relativessByNameSet = personsByName.get(fullName);
			if (relativessByNameSet == null) {
				relativessByNameSet = new HashSet<>();
				personsByName.put(fullName, relativessByNameSet);
			}
			relativessByNameSet.add(person);
			addTofirstLevelRelativePeople(person, relativessByNameSet);
			
		} 
		
	}


	private void addTofirstLevelRelativePeople(Person person, Set<Person> personsBySet) {
		LOG.info("Adding " + person + " to Graph");
		for (Person firstLevelRelativePerson : personsBySet) {
			if (!firstLevelRelativePerson.equals(person)) {
				Set<Person> firstLevelRelativePeople = personsGraph.get(firstLevelRelativePerson);
				if (!firstLevelRelativePeople.contains(person)) {
					firstLevelRelativePeople.add(person);
					LOG.info(" + " +person + " Added to " + firstLevelRelativePerson + " first level Relatives");
				}
				else {
					LOG.info(" - " + person + " already contains in " + firstLevelRelativePerson + " first level Relatives");
				}
			}
				
		}
	}

	public int findMinRelationLevel(Person personA, Person personB) {
		int level = -1;

		Set<Person> visitedSet = new HashSet<>();
		Set<Person> relativesSet = personsGraph.get(personA);
		if (personA.equals(personB)) {
			level = 0;
		}
		else {
			int digInGraph = digInGraph(personB, relativesSet, visitedSet, null);
			level = digInGraph > 0 ? digInGraph : -1;
		}
		
		return level;		
	}
	
	private int digInGraph(Person personB, Set<Person> relativesSet, Set<Person> visitedSet, Person startPerson) {
		// find shortest route  	
		Map<Person, Integer> startPersons2LevelsMap = new HashMap<>();
		int level = 0;
		if (relativesSet.contains(personB)) {
			level = 1;
		}
		else {
			for (Person person : relativesSet) {
				if (!visitedSet.contains(person)) {
					Set<Person> nextRelativesSet = personsGraph.get(person);
					level += digInGraph(personB, nextRelativesSet, visitedSet, startPerson == null ? person : startPerson);
					visitedSet.add(person);
					if (startPerson == null) {
						startPersons2LevelsMap.put(person, level > 0 ? level + 1 : 0);
						level = 0;
					}
				}
			}
		}
		
		if (startPerson == null && !startPersons2LevelsMap.isEmpty()) {
			int bestLevel = Integer.MAX_VALUE;
			for (int levelInMap : startPersons2LevelsMap.values()) {				
				bestLevel = Math.min(levelInMap, bestLevel);
			}
			level = bestLevel;
		}
		return level;		
	}
	

	@Override
	public String toString() {
		return "PersonsGraph = " + personsGraph;
	}
	
}
