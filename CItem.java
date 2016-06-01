package com.liheng.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CItem {
	private List<String> ItemSetA = new ArrayList<String>();
	private List<String> ItemSetB = new ArrayList<String>();
	private double Confidence = 0;
	

	public double getConfidence() {
		return Confidence;
	}
	public void setConfidence(double confidence) {
		Confidence = confidence;
	}
	public List<String> getItemSetA() {
		return ItemSetA;
	}
	public void setItemSetA(List<String> itemSetA) {
		ItemSetA = itemSetA;
	}
	public List<String> getItemSetB() {
		return ItemSetB;
	}
	public void setItemSetB(List<String> itemSetB) {
		ItemSetB = itemSetB;
	}
}
