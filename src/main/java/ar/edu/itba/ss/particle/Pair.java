package ar.edu.itba.ss.particle;

import java.util.function.Function;

public class Pair {
	
	public double x, y;
	
	public Pair(double x, double y){
		this.x=x;
		this.y=y;
	}
	
	public void add(Pair p){
		this.x += p.x;
		this.y += p.y;
	}

	public void applyFunction(Function<Double, Double> f){
		x = f.apply(x);
		y = f.apply(y);
	}

	public String toString(){
		return "("+x+", "+y+")";
	}
	
	public Pair clone() {
		return new Pair(x, y);
	}
	
	static public Pair sum(Pair p1, Pair p2){
		return new Pair(p1.x+p2.x, p1.y+p2.y);
	}

	public void multiply(double coef){
		this.applyFunction(cord->coef*cord);
	}
	
	static public Pair less(Pair p1, Pair p2){
		return new Pair(p1.x-p2.x, p1.y-p2.y);
	}

	static public double abs(Pair p){
		return Math.sqrt(abs2(p));
	}

	public double abs(){
		return Pair.abs(this);
	}
	
	static public double abs2(Pair p){
		return p.x*p.x+p.y*p.y;
	}
	
	static public double dist2(Pair p1, Pair p2){
		double distX = (p1.x - p2.x);
		double distY = (p1.y - p2.y);
		return distX * distX + distY * distY;
	}

	static public double internalProd(Pair p1, Pair p2){
		return p1.x*p2.x+p1.y*p2.y;
	}

	public void normalize() {
		double norm = abs(this);
		applyFunction(x->x/norm);
	}

	public void reset(double x, double y){
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Pair substract(Pair p) {

		return new Pair(this.x - p.x, this.y - p.y);
	}
}
