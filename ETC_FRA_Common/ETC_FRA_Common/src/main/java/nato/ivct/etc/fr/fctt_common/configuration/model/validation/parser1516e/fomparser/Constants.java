package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

public class Constants {

   public static enum endianness {
      Little, Big
   };


   public static enum publishSubscribe {
      Neither, Publish, Subscribe, PublishSubscribe
   };


   // Marshalling Metadatas
   static public final String Metadata_Category_HLA_Encoding = "HLA_Encoding";


   static public final String Metadata_Endianness = "Endianness";


   static public final String Metadata_Representation = "Representation";


   static public final String Metadata_Padding = "Padding";


   static public final String Metadata_NullTerminated = "NullTerminated";


   static public final String Metadata_Distribution = "Distribution";


   static public final String Metadata_MarshallingClass = "MarshallingClass";


   static public final String Metadata_MarshallingBitBoundary = "MarshallingBitBoundary";


   public static final String Metadata_DocPrefix = "__doc__";

   public static final String METADATA_ARRAY_LENGTH_ATTRIBUTE = "ArrayLengthAttribute";


   // Synchronisation Metadatas
   static public final String Metadata_Category_HLA_Synchonisation = "HLA_Synchro";


   public final static String Metadata_Regulated_ITCS = "Synchro_Time_Regulated_ITCS";


   public final static String Metadata_Regulated_ITCS_DefaultValue = "false";


   public final static String Metadata_Constrained_ITCS = "Synchro_Time_Constrained_ITCS";


   public final static String Metadata_Constrained_ITCS_DefaultValue = "false";


   public final static String Metadata_Regulated_Ext = "Synchro_Time_Regulated_Ext";


   public final static String Metadata_Regulated_Ext_DefaultValue = "false";


   public final static String Metadata_Constrained_Ext = "Synchro_Time_Constrained_Ext";


   public final static String Metadata_Constrained_Ext_DefaultValue = "false";


   public final static String Metadata_Frequency = "Synchro_DelaTime";


   public final static String Metadata_Frequency_DefaultValue = "10";


   // RealTime Metadatas

   public final static String Metadata_Realtime = "Synchro_Realtime";


   public final static String Metadata_Realtime_DefaultValue = "false";


   public static final String Metadata_RealTimeFactor = "Synchro_RealTime_Factor";


   public static final String Metadata_RealTimeFactor_DefaultValue = "1.0";


   /*
    * public final static String Metadata_AccelerationFactor =
    * "Synchro_AccelerationFactor";
    *
    *
    * public final static String Metadata_AccelerationFactor_DefaultValue =
    * "1";
    */


   // Synchronisation Points Metadatas

   public final static String Metadata_SynchroPoints_AutoAchieve = "SynchroPoints_AutoAchieve";


   public final static String Metadata_SynchroPoints_AutoAchieve_DefaultValue = "true";


   public final static String Metadata_SynchroPoints_MappingITCS = "SynchroPoints_MappingITCS";


   public final static String Metadata_SynchroPoints_MappingExt = "SynchroPoints_MappingExt";


   public static final String synchroPointsSeparator = "@@separator@@";


}
