package sk.berops.android.fueller.dataModel.maintenance;

import org.simpleframework.xml.Element;

public abstract class GenericItem {
	
	@Element(name="producer")
	private String producer;
	@Element(name="partID")
	private String partID;
	@Element(name="price")
	private double price;
	//@Element(category="category")
	//private ??? category 
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getPartID() {
		return partID;
	}
	public void setPartID(String partID) {
		this.partID = partID;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
}
