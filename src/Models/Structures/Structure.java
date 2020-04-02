package Models.Structures;

import DataManager.Streets;
import DataManager.Towns;
import Models.Street;
import Models.Town;
import Models.district.District;
import Models.spawnpoint.SpawnpointStructure;

public class Structure
{
	protected int id;
	protected String name;
	protected int streetID;
	protected Street street;
	protected int streetNumber;
	protected int townID;
	protected Town town;
	protected int districtID;
	protected District district;
	protected SpawnpointStructure spawnpoint;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStreetID() {
		return this.streetID;
	}

	public void setStreetID(int streetID) {
		this.streetID = streetID;
	}

	public Street getStreet() {
		return this.street;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public int getStreetNumber() {
		return this.streetNumber;
	}

	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}

	public int getTownID() {
		return this.townID;
	}

	public void setTownID(int townID) {
		this.townID = townID;
	}

	public Town getTown() {
		return this.town;
	}

	public void setTown(Town town) {
		this.town = town;
	}

	public int getDistrictID() {
		return this.districtID;
	}

	public void setDistrictID(int districtID) {
		this.districtID = districtID;
	}

	public District getDistrict() {
		return this.district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public SpawnpointStructure getSpawnpointID() {
		return this.spawnpoint;
	}

	public void setSpawnpointID(SpawnpointStructure spawnpoint) {
		this.spawnpoint = spawnpoint;
	}
	
	public Structure(int id, 
			String name, 
			int streetID, 
			int streetNumber, 
			int townID, 
			int districtID, 
			SpawnpointStructure spawnpoint)
	{
		this.id = id;
		this.name = name;
		this.streetID = streetID;
		this.street = Streets.FindStreet(streetID);
		this.townID = townID;
		this.town = Towns.FindTown(townID);
		this.districtID = districtID;
		this.spawnpoint = spawnpoint;
	}
}
