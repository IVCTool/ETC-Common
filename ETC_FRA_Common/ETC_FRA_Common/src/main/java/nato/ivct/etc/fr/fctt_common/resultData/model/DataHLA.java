package nato.ivct.etc.fr.fctt_common.resultData.model;

import nato.ivct.etc.fr.fctt_common.mainWindow.model.IObjectHLA;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelDataHLAType;
import nato.ivct.etc.fr.fctt_common.utils.FCTT_Enum.eModelState;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataHLA implements IObjectHLA
{
	/**
	 * Name of the data
	 */
	private StringProperty mName;
	
	/**
	 * Number of sending of the data (Published)
	 */
	private IntegerProperty mSentCount;
	
	/**
	 * Current state for the sending
	 */
	private ObjectProperty<eModelState> mSendingState;
	
	/**
	 * Old state for the sending
	 */
	private ObjectProperty<eModelState> mOldSendingState;
	
	/**
	 * Number of reception of the data (Subscribed)
	 */
	private IntegerProperty mReceivedCount;
	
	/**
	 * Current state of the reception
	 */
	private ObjectProperty<eModelState> mReceptionState;
	
	/**
	 * Old state of the reception
	 */
	private ObjectProperty<eModelState> mOldReceptionState;
	
	/**
	 * Children of the data
	 */
	private ObservableList<DataHLA> mChildren;
	
	/**
	 * Type of the data
	 */
	private ObjectProperty<eModelDataHLAType> mDataType;
	
	/**
	 * Constructor
	 */
	public DataHLA()
	{
		mName = new SimpleStringProperty();
		mName.setValue("");
		
		mSentCount = new SimpleIntegerProperty();
		mSentCount.setValue(0);
		
		mSendingState = new SimpleObjectProperty<eModelState>();
		mSendingState.setValue(eModelState.NotConcerned);
		
		mOldSendingState = new SimpleObjectProperty<eModelState>();
		mOldSendingState.setValue(eModelState.NotConcerned);
		
		mReceivedCount = new SimpleIntegerProperty();
		mReceivedCount.setValue(0);
		
		mReceptionState = new SimpleObjectProperty<eModelState>();
		mReceptionState.setValue(eModelState.NotConcerned);
		
		mOldReceptionState = new SimpleObjectProperty<eModelState>();
		mOldReceptionState.setValue(eModelState.NotConcerned);
		
		mChildren = FXCollections.observableArrayList();
		
		mDataType = new SimpleObjectProperty<eModelDataHLAType>();
		mDataType.setValue(eModelDataHLAType.Root);
	}
	
	/**
	 * Constructor
	 * @param pName Name of the data
	 */
	public DataHLA(String pName)
	{
		mName = new SimpleStringProperty();
		mName.setValue(pName);
		
		mSentCount = new SimpleIntegerProperty();
		mSentCount.setValue(0);
		
		mSendingState = new SimpleObjectProperty<eModelState>();
		mSendingState.setValue(eModelState.NotConcerned);
		
		mOldSendingState = new SimpleObjectProperty<eModelState>();
		mOldSendingState.setValue(eModelState.NotConcerned);
		
		mReceivedCount = new SimpleIntegerProperty();
		mReceivedCount.setValue(0);
		
		mReceptionState = new SimpleObjectProperty<eModelState>();
		mReceptionState.setValue(eModelState.NotConcerned);
		
		mOldReceptionState = new SimpleObjectProperty<eModelState>();
		mOldReceptionState.setValue(eModelState.NotConcerned);
		
		mChildren = FXCollections.observableArrayList();
		
		mDataType = new SimpleObjectProperty<eModelDataHLAType>();
		mDataType.setValue(eModelDataHLAType.Root);
	}
		
	public StringProperty nameProperty()
	{
		return mName;
	}
	
	public IntegerProperty sentCountProperty()
	{
		return mSentCount;
	}
	
	public ObjectProperty<eModelState> sendingStateProperty()
	{
		return mSendingState;
	}
	
	public ObjectProperty<eModelState> oldSendingStateProperty()
	{
		return mOldSendingState;
	}
	
	public IntegerProperty receivedCountProperty()
	{
		return mReceivedCount;
	}
	
	public ObjectProperty<eModelState> receptionStateProperty()
	{
		return mReceptionState;
	}
	
	public ObjectProperty<eModelState> oldReceptionStateProperty()
	{
		return mOldReceptionState;
	}
	
	public ObservableList<DataHLA> childrenProperty()
	{
		return mChildren;
	}
	
	public ObjectProperty<eModelDataHLAType> dataTypeProperty()
	{
		return mDataType;
	}
}