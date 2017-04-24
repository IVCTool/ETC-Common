package nato.ivct.etc.fr.fctt_common.utils;

import javafx.geometry.Pos;
import javafx.stage.Window;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;

public class FCTT_Notification 
{
	public enum eLevelNotification { info, warn, error };
	
	public static void displayNotification(Window pOwner, String pText, eLevelNotification pLevel)
	{
		Notifications lNotificationDialog = Notifications.create();
		lNotificationDialog.owner(pOwner);
		lNotificationDialog.hideAfter(Duration.seconds(3));
		lNotificationDialog.text(pText);
		lNotificationDialog.position(Pos.CENTER);
		
		if (pLevel == eLevelNotification.info)
		{
			lNotificationDialog.showInformation();
		}
		
		if (pLevel == eLevelNotification.warn)
		{
			lNotificationDialog.showWarning();
		}
		
		if (pLevel == eLevelNotification.error)
		{
			lNotificationDialog.showError();
		}
	}
}