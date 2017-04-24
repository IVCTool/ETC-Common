package nato.ivct.etc.fr.fctt_common.utils;

public class FCTT_Enum 
{
	public enum eModelState { NotExpectedNotSeen, NotExpectedSeen, ExpectedNotSeen, ExpectedSeen, NotConcerned, NoInformation };
	public enum eModelDataHLAType { Object, Interaction, Attribute, Root };
	public enum eModelDataHLAUpdatingWay { Send, Receive };
	public enum eModelServiceHLAType { Group, Service, Root };
	public enum eBuildResults { MaxLengthServices, MaxLengthData, ServicesCertificated, ServicesNotCertificated, DataCertificated, DataNotCertificated };
}