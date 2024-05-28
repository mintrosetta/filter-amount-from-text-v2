package model;

public class TestModel {
	private String text;
	private double result;
	
	public TestModel(String text, double result) {
		this.text = text;
		this.result = result;
	}
	
	public String getText() {
		return this.text;
	}
	
	public double getResult() {
		return this.result;
	}
}
