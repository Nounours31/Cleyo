package sfa.dev.maree.harmonique.model;

import java.util.Date;


public class PointDeMeusureMaree implements Comparable<PointDeMeusureMaree>
{
	private Date 						_horaireMaree;
	private double						_hauteurEau;


	public PointDeMeusureMaree(Date d, double h) 
	{
		super();
		this._horaireMaree = d;
		this._hauteurEau = h;
	}


	public Date get_horaireMaree() {
		return _horaireMaree;
	}


	public double get_hauteurEau() {
		return _hauteurEau;
	}


	@Override
	public int compareTo(PointDeMeusureMaree o) {
		return this._horaireMaree.compareTo(o._horaireMaree);
	}
}
