package sfa.dev.maree.mains;

import java.io.IOException;
import java.text.ParseException;

import sfa.dev.maree.harmonique.model.computeportinfo.MareeHarmoniqueTools;
import sfa.dev.maree.tools.MareeEnv;



public class ComputeHarmoniqueDeUnPort {
	public static void main(String[] args) throws ParseException, IOException {
		MareeHarmoniqueTools x = new MareeHarmoniqueTools();
		x.computeHarmonique(MareeEnv.IdREFMAR_BREST);
	}
	
}
