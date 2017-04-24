package nato.ivct.etc.fr.fctt_common.configuration.model.validation.parser1516e.fomparser;

import fr.itcs.sme.architecture.technical.types.INative;
import fr.itcs.sme.architecture.technical.types.INativeEnum;


/**
 * JavaTypeEquivalent.java declaration.
 *
 * @author sdudoit
 */
public class HLA1516JavaEquivalence {
  
    
    /**
     * Set the Java equivalence for the given HLA 1516 type
	 * @param type HLA data type
	 * @param endian Endian-ness
	 * @return Number of padding bytes
     */
    public static int set(INative type, String endian) {
 //       SendTrace.sendDebug(
    	int lNbPaddingBytes=0;
        if (type.getName().equals("HLAoctet")) { //$NON-NLS-1$
            type.setNative(INativeEnum.BYTE);
            lNbPaddingBytes=1;
        } else if (type.getName().equals("HLAoctetPairBE") //$NON-NLS-1$
                || type.getName().equals("HLAoctetPairLE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.CHARACTER);
            lNbPaddingBytes=2;
        } else if (type.getName().equals("UnsignedInteger8BE") //$NON-NLS-1$
        		|| type.getName().equals("RPRunsignedInteger8BE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.BYTE);
            lNbPaddingBytes=1;
        } else if (type.getName().equals("HLAinteger16BE") //$NON-NLS-1$
                || type.getName().equals("HLAinteger16LE") //$NON-NLS-1$
                || type.getName().equals("UnsignedInteger16BE") //$NON-NLS-1$
                || type.getName().equals("RPRunsignedInteger16BE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.SHORT);
            lNbPaddingBytes=2;
        } else if (type.getName().equals("HLAinteger32BE") //$NON-NLS-1$
                || type.getName().equals("HLAinteger32LE") //$NON-NLS-1$
                || type.getName().equals("UnsignedInteger32BE") //$NON-NLS-1$
                || type.getName().equals("RPRunsignedInteger32BE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.INTEGER);
            lNbPaddingBytes=4;
        } else if (type.getName().equals("HLAinteger64BE") //$NON-NLS-1$
                || type.getName().equals("HLAinteger64LE") //$NON-NLS-1$
                || type.getName().equals("UnsignedInteger64BE") //$NON-NLS-1$
                || type.getName().equals("RPRunsignedInteger64BE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.LONG);
            lNbPaddingBytes=8;
        } else if (type.getName().equals("HLAfloat32BE") //$NON-NLS-1$
                || type.getName().equals("HLAfloat32LE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.FLOAT);
            lNbPaddingBytes=4;
        } else if (type.getName().equals("HLAfloat64BE") //$NON-NLS-1$
                || type.getName().equals("HLAfloat64LE")) { //$NON-NLS-1$
            type.setNative(INativeEnum.DOUBLE);
            lNbPaddingBytes=8;
        } else if (type.getName().equals("OMT13any")) { //$NON-NLS-1$
            type.setNative(INativeEnum.OBJECT);
            lNbPaddingBytes=0;
        } else if (type.getName().equals("OMT13boolean")) { //$NON-NLS-1$
            type.setNative(INativeEnum.BOOLEAN);
            lNbPaddingBytes=1;
        } else {
//            SendTrace.sendDebug("No Java equivalence found for : " + type.getName()); //$NON-NLS-1$
        }        
        // Set endianness
        
        
        if (endian != null) {      	
        	Utils.addMetadata(Constants.Metadata_Endianness, endian, type);
        }
        
 //   	System.err.println("HLA1516JavaEquivalence::set : "+type.getName()+" endian="+endian+" equivalent="+type.getNative().getLiteral()); //$NON-NLS-1$ //$NON-NLS-2$
        return lNbPaddingBytes;
    }

    
    /**
     * Set the Java type equivalent for the given HLA 1516 type
     */
 /*   public static void set(IEnum type, String endian) {
        Metadata meta=BaseFactory.eINSTANCE.createMetadata();
        meta.setName(Constants.MetadataEndianness);
        if (endian != null) {
            if (endian.equals("Big")) { //$NON-NLS-1$
            	meta.setValue(Constants.endianness.Big.name());
            } else if (endian.equals("Little")) { //$NON-NLS-1$
            	meta.setValue(Constants.endianness.Little.name());
            }
        }
        type.getMetadatas().add(meta);

    }    */
    
}
