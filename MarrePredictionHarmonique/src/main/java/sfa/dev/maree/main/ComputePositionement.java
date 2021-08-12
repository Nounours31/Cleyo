package sfa.dev.maree.main;

import sfa.dev.generique.tools.E4ALogger;
import sfa.dev.positionement.tools.Coordonees;
import sfa.dev.positionement.tools.Positionement;

public class ComputePositionement {
	public static void main(String[] args) throws Exception {
		E4ALogger.setEnvLevel("Debug");
		
		
		// 50°03'59.0"N 5°42'53.0"W -- 50.066402653094954, -5.714561269207355
		// 58°38'38.0"N 3°04'12.0"W -- 58.64386598982483, -3.0698623988646836
		Coordonees P1 = new Coordonees(50.066402653094954, -5.714561269207355);
		Coordonees P2 = new Coordonees(58.64386598982483, -3.0698623988646836);
		
		
		Positionement p = new Positionement();
		System.out.println("OrthoDromie");
		System.out.println(p.capOrthoDromieDeuxPoint(P1, P2)); // 9.11 degre
		System.out.println(p.distanceOrthodromieDeuxPoint(P1, P2)); // 969.934 km

		// loxodromie - path of constant bearing
		System.out.println("LoxoDromie");
		System.out.println(p.capLoxodromieDeuxPoint(P1, P2)); // 10.14 degre
		System.out.println(p.distanceLoxodromieDeuxPoint(P1, P2)); // 969.991 km

		
		
		Coordonees P3 = new Coordonees(50.366187026887175, -4.134068684233742);
		Coordonees P4 = new Coordonees(42.35100920759794, -71.04083577167245);
		System.out.println("OrthoDromie");
		System.out.println(p.capOrthoDromieDeuxPoint(P3, P4)); // 9.11 degre
		System.out.println(p.distanceOrthodromieDeuxPoint(P3, P4)); // 969.934 km

		// loxodromie - path of constant bearing
		System.out.println("LoxoDromie");
		System.out.println(p.capLoxodromieDeuxPoint(P3, P4)); // 10.14 degre
		System.out.println(p.distanceLoxodromieDeuxPoint(P3, P4)); // 969.991 km
	}
}
