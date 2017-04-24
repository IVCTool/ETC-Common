package nato.ivct.etc.fr.fctt_common.utils;

import nato.ivct.etc.fr.fctt_common.mainWindow.model.IObjectHLA;
import javafx.scene.control.TreeItem;

public class FCTTUtils 
{

	/**
	 * @param pTreeItem HLA object tree item
	 * @return item name
	 */
	public static String pathTreeItemName(TreeItem<IObjectHLA> pTreeItem)
	{
		String lReturnValue="";
		TreeItem<IObjectHLA> pParentItem;
		if (pTreeItem != null)
		{
			pParentItem = pTreeItem.getParent();
			if (pParentItem != null) 
			{
				if (pParentItem.getParent() == null) 
				{
					lReturnValue = pTreeItem.getValue().nameProperty().getValue();
				}
				else
				{
					lReturnValue = pathTreeItemName(pParentItem) + "." +  pTreeItem.getValue().nameProperty().getValue();
				}
			}	
		}	
		return lReturnValue;
	}

}
