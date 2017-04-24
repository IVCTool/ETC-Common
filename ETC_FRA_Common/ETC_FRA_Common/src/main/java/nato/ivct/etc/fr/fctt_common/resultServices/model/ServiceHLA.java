package nato.ivct.etc.fr.fctt_common.resultServices.model;

import java.util.ArrayList;
import java.util.List;

import nato.ivct.etc.fr.fctt_common.mainWindow.model.IObjectHLA;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelServiceHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Represents a HLA Service
 */
public class ServiceHLA implements IObjectHLA
{
	/**
	 * Name of the service
	 */
	private StringProperty mName;
	
	/**
	 * All methods name in lower case which matches with the service
	 */
	private List<String> mMethodsName;
	
	/**
	 * Current number of call of this service
	 */
	private IntegerProperty mCallCount;
	
	/**
	 * Children services if the current object represents a group of services and not a single service
	 */
	private ObservableList<ServiceHLA> mChildren;
	
	/**
	 * Current state of the service
	 */
	private ObjectProperty<eModelState> mState;
	
	/**
	 * Old state of the service
	 */
	private ObjectProperty<eModelState> mOldState;
	
	/**
	 * Type of the data
	 */
	private ObjectProperty<eModelServiceHLAType> mServiceType;
	
	/**
	 * Constructor
	 */
	public ServiceHLA()
	{
		super();

		mName = new SimpleStringProperty();
		mName.setValue("");
		
		mMethodsName = new ArrayList<String>();
		
		mCallCount = new SimpleIntegerProperty();
		mCallCount.setValue(0);
		
		mChildren = FXCollections.observableArrayList();
		
		mState = new SimpleObjectProperty<eModelState>();
		mState.setValue(eModelState.NotConcerned);
		
		mOldState = new SimpleObjectProperty<eModelState>();
		mOldState.setValue(eModelState.NotConcerned);
		
		mServiceType = new SimpleObjectProperty<eModelServiceHLAType>();
		mServiceType.setValue(eModelServiceHLAType.Root);
	}
	
	/**
	 * Constructor
	 * @param pName Name of the service
	 * @param pMethodsName Method name of the service
	 */
	public ServiceHLA(String pName, List<String> pMethodsName) 
	{
		this();
		mName = new SimpleStringProperty();
		mName.setValue(pName);
		
		if (pMethodsName != null)
		{
			mMethodsName = pMethodsName;
		}
		else
		{
			mMethodsName = new ArrayList<String>();
		}
		
		mCallCount = new SimpleIntegerProperty();
		mCallCount.setValue(0);
		
		mChildren = FXCollections.observableArrayList();
		
		mState = new SimpleObjectProperty<eModelState>();
		mState.setValue(eModelState.NotConcerned);
		
		mOldState = new SimpleObjectProperty<eModelState>();
		mOldState.setValue(eModelState.NotConcerned);
		
		mServiceType = new SimpleObjectProperty<eModelServiceHLAType>();
		mServiceType.setValue(eModelServiceHLAType.Root);
	}

	public StringProperty nameProperty()
	{
		return mName;
	}
	
	public List<String> methodsName()
	{
		return mMethodsName;
	}
	
	public IntegerProperty callCountProperty()
	{
		return mCallCount;
	}
	
	public ObjectProperty<eModelState> stateProperty()
	{
		return mState;
	}	
	
	public ObjectProperty<eModelState> oldStateProperty()
	{
		return mOldState;
	}	

	public ObservableList<ServiceHLA> childrenProperty()
	{
		return mChildren;
	}
	
	public ObjectProperty<eModelServiceHLAType> serviceTypeProperty()
	{
		return mServiceType;
	}
}