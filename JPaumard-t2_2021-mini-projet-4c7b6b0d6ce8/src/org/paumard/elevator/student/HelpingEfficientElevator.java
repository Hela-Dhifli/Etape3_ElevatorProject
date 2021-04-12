//BEN CHEIKH Makerem
//DHIFLI Hela
package org.paumard.elevator.student;
import org.paumard.elevator.Building;
import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HelpingEfficientElevator implements Elevator {
    private int currentFloor = 1;
    private final String id;
	private LocalTime time;
	
    private List<Integer> destinations = new ArrayList<>();
	private List<List<Person>> peopleByFloor;
	private List<Person> peopleInElevator  = new ArrayList<>();

	EfficientElevator efficient;

    @Override
    public void startsAtFloor(LocalTime time, int initialFloor) {
    	this.time = time;
		this.currentFloor = initialFloor;

	}

    @Override
    public void peopleWaiting(List<List<Person>> peopleByFloor) {
    	this.peopleByFloor = peopleByFloor;
    	this.efficient.peopleWaiting(peopleByFloor);
    }

    @Override
    public List<Integer> chooseNextFloors() {
       
    	if(! destinations.isEmpty()) {
    		return this.destinations;
    	}

    	if(countWaintingPeopleNotInFloorOne() > 0) {
    		List<Integer> nonEmptyFloors = findNonEmptyFloor();
    		int nonEmptyFloor = nonEmptyFloors.get(0);
    		if (nonEmptyFloor != this.currentFloor) {
    			return List.of(nonEmptyFloor);
    		} 
    		else {
    			int indexOfCurrentFloor = this.currentFloor - 1;
				List<Person> waitingListForCurrentFloor = 
						this.peopleByFloor.get(indexOfCurrentFloor);
				
				List<Integer> destinationFloorsForCurrentFloor = 
						findDestinationFloors(waitingListForCurrentFloor);
				this.setDestinations(destinationFloorsForCurrentFloor);
				return this.getDestinations();
    		}
    	}
    
    	if(this.efficient.isImBusy() && countWaintingPeopleInFloorOne() > 0) {
    		if(this.currentFloor == 1) {
    			List<Person> waitingListForCurrentFloor = 
						this.peopleByFloor.get(0);
				
				List<Integer> destinationFloorsForCurrentFloor = 
						findDestinationFloors(waitingListForCurrentFloor);
				this.destinations  = destinationFloorsForCurrentFloor;
				return this.destinations;
    		}
    	}
        return List.of(1);
    }
    
    private int countWaintingPeopleInFloorOne() {
    	return peopleByFloor.get(0).size();
	}
    
	private List<Integer> findNonEmptyFloor() {
		for (int indexFloor = 1 ; indexFloor < Building.MAX_FLOOR ; indexFloor++) {
			if (!peopleByFloor.get(indexFloor).isEmpty()) {
				return List.of(indexFloor + 1);
			}
		}
		return List.of(1);
	}
	
	private List<Integer> findDestinationFloors(List<Person> waitingListForCurrentFloor) {
		return waitingListForCurrentFloor.stream()
			.map(person -> person.getDestinationFloor())
			.distinct()
			.sorted()
			.collect(Collectors.toList());
	}
	
	public int countWaintingPeopleNotInFloorOne() {
		int numberPeopleWaiting = this.peopleByFloor.stream()
									.mapToInt(list -> list.size())
									.sum();
		int numberPeopleWaitingAtFloorOne = this.peopleByFloor
											.get(0).size();
		return (numberPeopleWaiting-numberPeopleWaitingAtFloorOne);
	}
	
	public int countWaintingCurrentFloor() {
		int numberPeopleWaiting = this.peopleByFloor.get(currentFloor-1).size();
		return (numberPeopleWaiting);
	}
	
    @Override
    public void arriveAtFloor(int floor) {
    	if (!this.getDestinations().isEmpty()) {
    		this.getDestinations().remove(0);
    	}
    	this.currentFloor = floor;
    }

    public void loadPeople(List<Person> people) {
    	this.peopleInElevator.addAll(people);
    	int indexFloor = this.currentFloor - 1;
    	this.peopleByFloor.get(indexFloor).removeAll(people);
    }

    @Override
    public void unload(List<Person> people) {
    	this.peopleInElevator.removeAll(people);
    }

    @Override
    public void newPersonWaitingAtFloor(int floor, Person person) {
    	int indexFloor = floor - 1;
    	this.peopleByFloor.get(indexFloor).add(person);
    }
    
    @Override
    public void lastPersonArrived() {
    }

    @Override
    public void timeIs(LocalTime time) {
    }

    @Override
    public void standByAtFloor(int currentFloor) {
    }
  	
    public HelpingEfficientElevator(int capacity, String id,EfficientElevator efficient) {
        this.id = id;
        this.efficient = efficient;
    }
    

	@Override
    public String getId() {
        return this.id;
    }

	public List<Integer> getDestinations() {
		return destinations;
	}
	
	public void setDestinations(List<Integer> destinations) {
		this.destinations = destinations;
	}
}
