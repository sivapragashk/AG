package com.hd.cedg.lms.model;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Cost implements Serializable {

	private static final long serialVersionUID = -273304154049410435L;

	private float cost;
	private float aligncost;

	public Cost() {
		cost = 0;
	}

	public Cost(float cost) {
		this.cost = cost;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getAligncost() {
		return aligncost;
	}

	public void setAligncost(float aligncost) {
		this.aligncost = aligncost;
	}

	public String getFormattedCost() {
		return getFormattedCost(true);
	}
	public String getFormattedAlignCost() {
		return getFormattedAlignCost(true);
	}

	public String getFormattedCost(boolean units) {
		DecimalFormat costFormat = new DecimalFormat("########0.00");
		return (units?"$":"") + costFormat.format(cost);
	}
	
	public String getFormattedAlignCost(boolean units) {
		DecimalFormat aligncostFormat = new DecimalFormat("########0.00");
		return (units?"$":"") + aligncostFormat.format(aligncost);
	}
}
