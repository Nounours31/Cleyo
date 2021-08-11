package sfa.dev.maree.harmonique.model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Ondes 
{
	public enum UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude 
	{
		zero, one,
		uminusM2, uMf, uO1, uM2, uM1, uJ1, uK1, uK2mQ1, u3M2, u2M2, u2M2mK2, uM2mK2, uGamma2, u2M2pK2, u4M2, uM2pK2, u3M2pK2, u5M2, u3M2mK2, u4M2mK2, uM2pK1, uK2, uM3, u2M2pK1, uM2mK1, uL2, uK1pJ1, uK2mM2,
		f2M2, f3M2, fMm, fM2, fMf, fO1, fM1, fJ1, fK1, fK2Q1, f2M2K2, fM2K2, fGamma2, fM2K1, fL2, fK2, fK1J1, f2M2K1, f4M2K2, f4M2, fM3, f3M2K2, f5M2, f6M2;
	}


	static public double _Z0 = 0.0;			        // valeur maoyenne

	//----------------------------------------------------------------------------------------------------------
	//-- Rq de PFS: les onde en commentaires sont celles qui n'ont pas d'info dans le fichier *.td4
	//-- pas l apeine de boucler dessus car l'amplitude est alors de 0.0
	//-- Par ailleur elles foutent la merde dans le calcul des harmoniques ....
	//----------------------------------------------------------------------------------------------------------
	static public Onde[] _table2NC = new Onde [] {
			//           T  s  h  p p1  deg     speed      u       f
			//						longues periodes
			new Onde ( "SA"      , 0, 0, 1, 0, 0,   0,  0.0410686, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "SSA"     , 0, 0, 2, 0, 0,   0,  0.0821373, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			//new ConstituantHarmoniqueUneOnde ( "MSM"     , 0, 1,-2, 1, 0,   0,  0.4715211, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),   // MNum = M2-Nu2
			// new ConstituantHarmoniqueUneOnde ( "MM"      , 0, 1, 0,-1, 0,   0,  0.5443747, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fMm),
			new Onde ( "MSF"     , 0, 2,-2, 0, 0,   0,  1.0158958, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uminusM2,UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),    // S2-M2
			new Onde ( "MF"      , 0, 2, 0, 0, 0,   0,  1.0980331, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uMf,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fMf),
			//								diurnes
			new Onde ( "2Q1"     , 1,-4, 1, 2, 0, -90, 12.8542862, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "SIGMA1"  , 1,-4, 3, 0, 0, -90, 12.9271398, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "Q1"      , 1,-3, 1, 1, 0, -90, 13.3986609, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "RHO1"    , 1,-3, 3,-1, 0, -90, 13.4715145, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "O1"      , 1,-2, 1, 0, 0, -90, 13.9430356, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "MS1"     , 1,-2, 2, 0, 0,+180, 13.9841042, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),    // M2-S1
			new Onde ( "MP1"     , 1,-2, 3, 0, 0, +90, 14.0251729, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			//new ConstituantHarmoniqueUneOnde ( "M1"      , 1,-1, 1, 1, 0, +90, 14.4966939, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM1),
			//new ConstituantHarmoniqueUneOnde ( "CHI1"    , 1,-1, 3,-1, 0, +90, 14.5695476, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uJ1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fJ1),
			new Onde ( "PI1"     , 1, 0,-2, 0, 1, -90, 14.9178647, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "P1"      , 1, 0,-1, 0, 0, -90, 14.9589314, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "S1"      , 1, 0, 0, 0, 0, -90, 15.0      , UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "K1"      , 1, 0, 1, 0, 0, +90, 15.0410686, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK1),
			new Onde ( "PSI1"    , 1, 0, 2, 0,-1, +90, 15.0821353, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			// new ConstituantHarmoniqueUneOnde ( "PHI1"    , 1, 0, 3, 0, 0, +90, 15.1232059, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uJ1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fJ1),
			// new ConstituantHarmoniqueUneOnde ( "THETA1"  , 1, 1,-1, 1, 0, +90, 15.5125897, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uJ1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fJ1),
			new Onde ( "J1"      , 1, 1, 1,-1, 0, +90, 15.5854433, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uJ1,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fJ1),
			new Onde ( "OO1"     , 1, 2, 1, 0, 0, +90, 16.1391017, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2mQ1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK2Q1), //like KQ1=K2-Q1
			//	                        semi diurnes
			new Onde ( "2MN2S2"  , 2,-7, 6, 1, 0,   0, 26.4079380, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "2NS2"    , 2,-6, 4, 2, 0,   0, 26.8794591, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "3M2S2"   , 2,-6, 6, 0, 0,   0, 26.9523127, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "OQ2"     , 2,-5, 2, 1, 0,   0, 27.3416965, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2mK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2), // MNK2
			new Onde ( "MNS2"    , 2,-5, 4, 1, 0,   0, 27.4238337, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "MNUS2"   , 2,-5, 6,-1, 0,   0, 27.4966874, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "2MK2"    , 2,-4, 2, 0, 0,   0, 27.8860712, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2mK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2), // 2M2-K2
			new Onde ( "2N2"     , 2,-4, 2, 2, 0,   0, 27.8953548, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "MU2"     , 2,-4, 4, 0, 0,   0, 27.9682084, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "N2"      , 2,-3, 2, 1, 0,   0, 28.4397295, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "NU2"     , 2,-3, 4,-1, 0,   0, 28.5125831, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "OP2"     , 2,-2, 0, 0, 0,   0, 28.9019670, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2mK2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),  // MSK2
			new Onde ( "GAMMA2"  , 2,-2, 0, 2, 0,+180, 28.9112506, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uGamma2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fGamma2),
			new Onde ( "M(SK)2"  , 2,-2, 1, 0,+1,+180, 28.9430375, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2mK1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K1),  // M2+S1-K1
			new Onde ( "M2"      , 2,-2, 2, 0, 0,   0, 28.9841042, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "M(KS)2"  , 2,-2, 3, 0,-1,   0, 29.0251709, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K1),  // M2+K1-S1
			new Onde ( "MKS2"    , 2,-2, 4, 0, 0,   0, 29.0662415, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),
			new Onde ( "LAMBDA2" , 2,-1, 0, 1, 0,+180, 29.4556253, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "L2"      , 2,-1, 2,-1, 0,+180, 29.5284789, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uL2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fL2),    // 2MN2
			new Onde ( "NKM2"    , 2,-1, 2, 1, 0,   0, 29.5377625, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2), //N2+K2-M2
			new Onde ( "T2"      , 2, 0,-1, 0, 1,   0, 29.9589333, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "S2"      , 2, 0, 0, 0, 0,   0, 30.0      , UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "R2"      , 2, 0, 1, 0,-1,+180, 30.0410667, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "K2"      , 2, 0, 2, 0, 0,   0, 30.0821373, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK2),
			new Onde ( "MSN2"    , 2, 1, 0,-1, 0,   0, 30.5443747, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),   // ETA2
			new Onde ( "KJ2"     , 2, 1, 2,-1, 0,   0, 30.6265120, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK1pJ1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK1J1),  // K1+J1
			new Onde ( "2SM2"    , 2, 2,-2, 0, 0,   0, 31.0158958, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uminusM2,UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "SKM2"    , 2, 2, 0, 0, 0,   0, 31.0980331, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2mM2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),
			//								tiers diurnes
			new Onde ( "2MK3"    , 3,-4, 3, 0, 0, -90, 42.9271398, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2pK1,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K1), // 2M2+K1
			new Onde ( "M3"      , 3,-3, 3, 0, 0,+180, 43.4761563, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM3,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM3),
			// new ConstituantHarmoniqueUneOnde ( "SO3"     , 3,-2, 1, 0, 0, -90, 43.9430356, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uO1,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fO1),
			new Onde ( "MS3"     , 3,-2, 2, 0, 0,+180, 43.9841042, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "MK3"     , 3,-2, 3, 0, 0, +90, 44.0251729, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK1,   UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K1),
			new Onde ( "SP3"     , 3, 0,-1, 0, 0,   0, 44.9589314, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "S3"      , 3, 0, 0, 0, 0,   0, 45.0      , UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "SK3"     , 3, 0, 1, 0, 0, +90, 45.0410686, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK1,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK1),
			//								quart diurnes
			//new ConstituantHarmoniqueUneOnde ( "2MMUS4"  , 4,-8, 8, 0, 0,   0, 55.9364168, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "2MNS4"   , 4,-7, 6, 1, 0,   0, 56.4079380, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "N4"      , 4,-6, 4, 2, 0,   0, 56.8794591, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "3MS4"    , 4,-6, 6, 0, 0,   0, 56.9523127, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "MN4"     , 4,-5, 4, 1, 0,   0, 57.4238337, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "MNU4"    , 4,-5, 6,-1, 0,   0, 57.4966874, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "2MSK4"   , 4,-4, 2, 0, 0,   0, 57.8860712, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2mK2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2),// 2M2+S2-K2
			new Onde ( "M4"      , 4,-4, 4, 0, 0,   0, 57.9682084, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			//new ConstituantHarmoniqueUneOnde ( "2MKS4"   , 4,-4, 6, 0, 0,   0, 58.0503457, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2pK2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2),// 2M2+K2-S2
			new Onde ( "SN4"     , 4,-3, 2, 1, 0,   0, 58.4397295, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "3MN4"    , 4,-3, 4,-1, 0,   0, 58.5125832, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),  // ML4
			new Onde ( "NK4"     , 4,-3, 4, 1, 0,   0, 58.5218667, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK2,   UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),
			new Onde ( "MT4"     , 4,-2, 1, 0, 1,   0, 58.9430375, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "MS4"     , 4,-2, 2, 0, 0,   0, 58.9841042, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "MK4"     , 4,-2, 4, 0, 0,   0, 59.0662415, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK2,   UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),
			new Onde ( "2SNM4"   , 4,-1, 0, 1, 0,   0, 59.4556253, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "2MSN4"   , 4,-1, 2,-1, 0,   0, 59.5284789, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "S4"      , 4, 0, 0, 0, 0,   0, 60.0      , UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.zero,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.one),
			new Onde ( "SK4"     , 4, 0, 2, 0, 0,   0, 60.0821372, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uK2,      UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fK2),
			//								6 eme diurnes
			new Onde ( "3MNK6"   , 6,-9, 6, 1, 0,   0, 85.3099049, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2mK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2K2),
			new Onde ( "3MNS6"   , 6,-9, 8, 1, 0,   0, 85.3920422, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "3MNUS6"  , 6,-9,10,-1, 0,   0, 85.4648958, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "4MK6"    , 6,-8, 6, 0, 0,   0, 85.8542796, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2mK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2K2),
			new Onde ( "2NM6"    , 6,-8, 6, 2, 0,   0, 85.8635632, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "4MS6"    , 6,-8, 8, 0, 0,   0, 85.9364169, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "2MN6"    , 6,-7, 6, 1, 0,   0, 86.4079380, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "2MNU6"   , 6,-7, 8,-1, 0,   0, 86.4807915, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "3MSK6"   , 6,-6, 4, 0, 0,   0, 86.8701754, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2mK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2K2),
			new Onde ( "M6"      , 6,-6, 6, 0, 0,   0, 86.9523127, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "3MKS6"   , 6,-6, 8, 0, 0,   0, 87.0344499, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2pK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2K2),
			new Onde ( "MSN6"    , 6,-5, 4, 1, 0,   0, 87.4238337, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "4MN6"    , 6,-5, 6,-1, 0,   0, 87.4966874, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f5M2),   // 2ML6
			new Onde ( "MNK6"    , 6,-5, 6, 1, 0,   0, 87.5059709, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2pK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2), //M2+N2+K2
			new Onde ( "2MT6"    , 6,-4, 3, 0, 1,   0, 87.9271417, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "2MS6"    , 6,-4, 4, 0, 0,   0, 87.9682084, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			new Onde ( "2MK6"    , 6,-4, 6, 0, 0,   0, 88.0503457, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2pK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2K2),
			new Onde ( "2SN6"    , 6,-3, 2, 1, 0,   0, 88.4397295, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "3MSN6"   , 6,-3, 4,-1, 0,   0, 88.5125831, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			//new ConstituantHarmoniqueUneOnde ( "3MKN6"   , 6,-3, 6,-1, 0,   0, 88.5947203, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2pK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2K2), // 3M2+K2-N2
			new Onde ( "2SM6"    , 6,-2, 2, 0, 0,   0, 88.9841042, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2,     UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2),
			new Onde ( "MSK6"    , 6,-2, 4, 0, 0,   0, 89.0662415, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.uM2pK2,  UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.fM2K2),
			//								8 eme diurnes
			// new ConstituantHarmoniqueUneOnde ( "2(MN)8"  , 8,-10,8, 2, 0,   0, 114.8476675, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "3MN8"    , 8,-9, 8, 1, 0,   0, 115.3920422, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			// new ConstituantHarmoniqueUneOnde ( "3MNU8"   , 8,-9,10,-1, 0,   0, 115.4648957, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "M8"      , 8,-8, 8, 0, 0,   0, 115.9364169, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			//new ConstituantHarmoniqueUneOnde ( "2MSN8"   , 8,-7, 6, 1, 0,   0, 116.4079380, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			new Onde ( "3MS8"    , 8,-6, 6, 0, 0,   0, 116.9523127, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2),
			//new ConstituantHarmoniqueUneOnde ( "3MK8"    , 8,-6, 8, 0, 0,   0, 117.0344499, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2pK2, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2K2),
			new Onde ( "4MSN8"   , 8,-5, 6,-1, 0,   0, 117.4966873, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f5M2),
			new Onde ( "2(MS)8"  , 8,-4, 4, 0, 0,   0, 117.9682084, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u2M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f2M2),
			//								10 eme diurnes
			new Onde ( "4MN10"   ,10,-11,10, 1, 0,  0, 144.3761463, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u5M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f5M2),
			new Onde ( "M10"     ,10,-10,10, 0, 0,  0, 144.9205210, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u5M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f5M2),
			new Onde ( "3MSN10"  ,10, -9, 8, 1, 0,  0, 145.3920421, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			new Onde ( "4MS10"   ,10, -8, 8, 0, 0,  0, 145.9364168, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f4M2),
			//new ConstituantHarmoniqueUneOnde ( "5MSN10"  ,10, -7, 8,-1, 0,  0, 146.4807915, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u4M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f6M2),
			new Onde ( "3M2S10"  ,10, -6, 6, 0, 0,  0, 146.9523126, UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.u3M2,    UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude.f3M2)
	};

	public static double eval(UcorrectionNodalePhase_and_FfacteurCorrectionNodaleAmplitude nomCorrection, double _p, double _N) 
	{
		switch (nomCorrection)
		{
		case zero: return zero(_p, _N); 
		case uminusM2: return uminusM2(_p, _N);
		case uMf: return uMf(_p, _N); 
		case uO1: return uO1(_p, _N);
		case uM2 : return uM2(_p, _N); 
		case uM1: return uM1(_p, _N); 
		case uJ1: return uJ1(_p, _N); 
		case uK1: return uK1(_p, _N); 
		case uK2mQ1: return uK2mQ1(_p, _N); 
		case u3M2: return u3M2(_p, _N); 
		case u2M2: return u2M2(_p, _N); 
		case u2M2mK2: return u2M2mK2(_p, _N); 
		case uM2mK2: return uM2mK2(_p, _N); 
		case uGamma2: return uGamma2(_p, _N); 
		case u2M2pK2: return u2M2pK2(_p, _N); 
		case u4M2: return u4M2(_p, _N); 
		case uM2pK2: return uM2pK2(_p, _N); 
		case u3M2pK2: return u3M2pK2(_p, _N); 
		case u5M2: return u5M2(_p, _N); 
		case u3M2mK2: return u3M2mK2(_p, _N); 
		case u4M2mK2: return u4M2mK2(_p, _N); 
		case uM2pK1: return uM2pK1(_p, _N); 
		case uK2: return uK2(_p, _N); 
		case uM3: return uM3(_p, _N); 
		case u2M2pK1: return u2M2pK1(_p, _N); 
		case uM2mK1: return uM2mK1(_p, _N); 
		case uL2: return uL2(_p, _N); 
		case uK1pJ1: return uK1pJ1(_p, _N); 
		case uK2mM2: return uK2mM2(_p, _N);
		case one: return one(_p, _N);
		case f2M2: return f2M2(_p, _N);
		case f3M2: return f3M2(_p, _N);
		case fMm: return fMm(_p, _N);
		case fM2: return fM2(_p, _N); 
		case fMf: return fMf(_p, _N); 
		case fO1: return fO1(_p, _N); 
		case fM1: return fM1(_p, _N); 
		case fJ1: return fJ1(_p, _N); 
		case fK1: return fK1(_p, _N); 
		case fK2Q1: return fK2Q1(_p, _N); 
		case f2M2K2: return f2M2K2(_p, _N); 
		case fM2K2: return fM2K2(_p, _N); 
		case fGamma2: return fGamma2(_p, _N); 
		case fM2K1: return fM2K1(_p, _N); 
		case fL2: return fL2(_p, _N); 
		case fK2: return fK2(_p, _N); 
		case fK1J1: return fK1J1(_p, _N); 
		case f2M2K1: return f2M2K1(_p, _N); 
		case f4M2K2: return f4M2K2(_p, _N); 
		case f4M2: return f4M2(_p, _N); 
		case fM3: return fM3(_p, _N); 
		case f3M2K2: return f3M2K2(_p, _N); 
		case f5M2: return f5M2(_p, _N); 
		case f6M2: return f6M2(_p, _N); 
		default: return 0.0;
		}
	}

	//---------------------------------------------------------------------------
	static private double zero(double p, double N) { return 0.; }
	static private double one(double p, double N)  { return 1.; }
	//---------------------------------------------------------------------------
	static private double cos_deg(double n2) 
	{
		return java.lang.Math.cos(Trigo.Deg2Rad * n2);
	}
	static private double sin_deg(double n2) {
		return java.lang.Math.sin(Trigo.Deg2Rad * n2);
	}


	static private double fMm(double p, double N)
	{ return 1.0 -0.1311*cos_deg(N) +0.0538*cos_deg(2.*p)+0.0205*cos_deg(2.*p-N); }
	static private double uMf(double p, double N) { return -23.7*sin_deg(N) +2.7*sin_deg(2.*N) -0.4*sin_deg(3.*N); }
	static private double fMf(double p, double N) { return 1.084 +0.415*cos_deg(N) +0.039*cos_deg(2.*N); }
	//---------------------------------------------------------------------------
	static private double uO1(double p, double N) { return 10.80*sin_deg(N) -1.34*sin_deg(2.*N) +0.19*sin_deg(3.*N);}
	static private double fO1(double p, double N) { return 1.0176 +0.1871*cos_deg(N) -0.0147*cos_deg(2.*N); }
	//---------------------------------------------------------------------------
	static private double uK1(double p, double N) { return -8.86*sin_deg(N) +0.68*sin_deg(2.*N) -0.07*sin_deg(3.*N);}
	static private double fK1(double p, double N) { return 1.0060 +0.1150*cos_deg(N)- 0.0088*cos_deg(2.*N)+ 0.0006*cos_deg(3.*N);}
	//---------------------------------------------------------------------------
	static private double uJ1(double p, double N) { return -12.94*sin_deg(N) +1.34*sin_deg(2.*N) -0.19*sin_deg(3.*N);}
	static private double fJ1(double p, double N) { return 1.1029 +0.1676*cos_deg(N) -0.0170*cos_deg(2.*N) +0.0016*cos_deg(3.*N); }
	//---------------------------------------------------------------------------
	static private double uM2(double p, double N) { return -2.14*sin_deg(N); }
	static private double fM2(double p, double N) { return 1.0007 -0.0373*cos_deg(N) + 0.0002*cos_deg(2.*N); }
	//---------------------------------------------------------------------------
	static private double uK2(double p, double N) { return -17.74*sin_deg(N) +0.68*sin_deg(2.*N) -0.04*sin_deg(3.*N);}
	static private double fK2(double p, double N) { return 1.0246 +0.2863*cos_deg(N) + 0.0083*cos_deg(2.*N) - 0.0015*cos_deg(3.*N); }
	//---------------------------------------------------------------------------
	static private double uM3(double p, double N) { return -3.21*sin_deg(N); }
	static private double fM3(double p, double N) { return java.lang.Math.pow(java.lang.Math.sqrt(fM2(p, N)),3); }
	//---------------------------------------------------------------------------
	static private double uminusM2(double p, double N) { return -uM2(p, N); }
	static private double u2M2(double p, double N)     { return 2.*uM2(p, N); }
	static private double u3M2(double p, double N)     { return 3.*uM2(p, N); }
	static private double u4M2(double p, double N)     { return 4.*uM2(p, N); }
	static private double u5M2(double p, double N)     { return 5.*uM2(p, N); }
	static private double u6M2(double p, double N)     { return 6.*uM2(p, N); }
	//---------------------------------------------------------------------------
	static private double f2M2(double p, double N) { return java.lang.Math.pow(fM2(p, N),2.); }
	static private double f3M2(double p, double N) { return java.lang.Math.pow(fM2(p, N),3.); }
	static private double f4M2(double p, double N) { return java.lang.Math.pow(fM2(p, N),4.); }
	static private double f5M2(double p, double N) { return java.lang.Math.pow(fM2(p, N),5.); }
	static private double f6M2(double p, double N) { return java.lang.Math.pow(fM2(p, N),6.); }
	//---------------------------------------------------------------------------
	// dans le nom des fonctions qui suivent, m sgnifie moins, p signifie plus
	static private double uM2mK1(double p, double N)  { return uM2(p, N)-uK1(p, N); }
	static private double uM2pK1(double p, double N)  { return uM2(p, N)+uK1(p, N); }
	static private double fM2K1(double p, double N)   { return fM2(p, N)*fK1(p, N); }
	static private double u2M2pK1(double p, double N) { return 2.*uM2(p, N)+uK1(p, N); }
	static private double f2M2K1(double p, double N)  { return java.lang.Math.pow(fM2(p, N),2.)*fK1(p, N); }
	//---------------------------------------------------------------------------
	static private double uM2mK2(double p, double N)  { return uM2(p, N)-uK2(p, N); }
	static private double uM2pK2(double p, double N)  { return uM2(p, N)+uK2(p, N); }
	static private double uK2mM2(double p, double N)  { return uK2(p, N)-uM2(p, N); }
	static private double fM2K2(double p, double N)   { return fM2(p, N)*fK2(p, N); }
	//---------------------------------------------------------------------------
	static private double u2M2mK2(double p, double N)  { return 2.*uM2(p, N)-uK2(p, N); }
	static private double u2M2pK2(double p, double N)  { return 2.*uM2(p, N)+uK2(p, N); }
	static private double f2M2K2(double p, double N)   { return java.lang.Math.pow(fM2(p, N),2.)*fK2(p, N); }
	//---------------------------------------------------------------------------
	static private double u3M2mK2(double p, double N)  { return 3.*uM2(p, N)-uK2(p, N); }
	static private double u3M2pK2(double p, double N)  { return 3.*uM2(p, N)+uK2(p, N); }
	static private double f3M2K2(double p, double N)   { return java.lang.Math.pow(fM2(p, N),3.)*fK2(p, N); }
	//---------------------------------------------------------------------------
	static private double u4M2mK2(double p, double N)  { return 4.*uM2(p, N)-uK2(p, N); }
	static private double f4M2K2(double p, double N)   { return java.lang.Math.pow(fM2(p, N),4)*fK2(p, N); }
	//---------------------------------------------------------------------------
	static private double uM1(double p, double N) { return Trigo.Rad2Deg*Math.atan2(sin_deg(p)+0.2*sin_deg(p-N) ,
			2.*(cos_deg(p)+0.2*cos_deg(p-N)) ); }
	static private double fM1(double p, double N) { return java.lang.Math.sqrt(Math.pow(sin_deg(p)+0.2*sin_deg(p-N),2) +
			java.lang.Math.pow(2.*(cos_deg(p)+0.2*cos_deg(p-N)),2)); }
	//---------------------------------------------------------------------------
	static private double uGamma2(double p, double N) { return Trigo.Rad2Deg*Math.atan2(0.147*sin_deg(2.*(N-p)) ,
			1.+0.147*cos_deg(2.*(N-p)) ); }
	static private double fGamma2(double p, double N) { return java.lang.Math.sqrt(Math.pow(0.147*sin_deg(2.*(N-p)),2) +
			java.lang.Math.pow(1.+0.147*cos_deg(2.*(N-p)),2)); }
	//---------------------------------------------------------------------------
	static private double uL2(double p, double N)
	{ return Trigo.Rad2Deg*java.lang.Math.atan2(
			-0.2505*sin_deg(2.*p)-0.1102*sin_deg(2.*p-N)-0.0156*sin_deg(2.*p-2.*N)-0.037*sin_deg(N),
			1.-0.2505*cos_deg(2.*p)-0.1102*cos_deg(2.*p-N)-0.0156*cos_deg(2.*p-2.*N)-0.037*cos_deg(N) ); }
	static private double fL2(double p, double N)
	{ return java.lang.Math.sqrt(
			java.lang.Math.pow(-0.2505*sin_deg(2.*p)-0.1102*sin_deg(2.*p-N)-0.0156*sin_deg(2.*p-2.*N)-0.037*sin_deg(N),2)+
			java.lang.Math.pow(1.-0.2505*cos_deg(2.*p)-0.1102*cos_deg(2.*p-N)-0.0156*cos_deg(2.*p-2.*N)-0.037*cos_deg(N),2) ); }
	//---------------------------------------------------------------------------
	static private double uK1pJ1(double p, double N)
	{ return uK1(p, N)+uJ1(p, N); }
	static private double fK1J1(double p, double N)
	{ return fK1(p, N)*fJ1(p, N); }
	//---------------------------------------------------------------------------
	static private double uK2mQ1(double p, double N)
	{ return uK2(p, N)-uO1(p, N); }
	static private double fK2Q1(double p, double N)
	{ return fK2(p, N)*fO1(p, N); }
	//---------------------------------------------------------------------------	


	private void initDataFromtd4(File paramFiletd4)
	{
		//-----------------------------------------------------------------------
		// cas particulier ou nous allons calculer les constante harmoniques ... 
		// elles n'existe pas encore !
		//-----------------------------------------------------------------------
		if (paramFiletd4 == null)
			return;

		try
		{
			InputStream is = new FileInputStream (paramFiletd4);
			BufferedReader in = new BufferedReader(new InputStreamReader (is));
			String Lue = null;

			int iIndice = 0;
			while ((Lue = in.readLine()) != null)
			{
				if (Lue.startsWith("\""))
					continue;

				if (Lue.startsWith("METRIC"))
					continue;

				Lue = Lue.replace ("\t", " ");
				String LueClean = Lue.replace("  ", " ");
				while (LueClean.length() != Lue.length())
				{
					Lue = LueClean;
					LueClean = Lue.replace("  ", " ");
				}
				String[] infos = Lue.split(" ");
				if (infos[0].equals("Z0"))
				{
					_Z0 = Double.parseDouble(infos[1]);
					continue;
				}

				iIndice = locateOndeIndice (infos[0]);
				if (iIndice < 0)
					throw new Exception("Invalide coef");

				_table2NC[iIndice]._ampli = Double.parseDouble(infos[1]);
				_table2NC[iIndice]._phase = Double.parseDouble(infos[2]);
				iIndice++;				
			}
			in.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}


	private int locateOndeIndice(String string) 
	{
		for (int i = 0; i < _table2NC.length; i++)
			if (string.equals(_table2NC[i]._Nom))
				return i;
		return -1;
	}

	public static void InitEquilibriumAndPhase(double s, double h, double p, double p1, double n) {
		for (int i = 0; i < _table2NC.length; i++)
			_table2NC[i].InitEquilibriumAndPhase(s, h, p, p1, n);
	}		
}
