using System;
using Tacticsoft;
using UnityEngine.UI;
using UnityEngine;
using UnityEngine.EventSystems;

public class SessionCell : TableViewCell
{
	public Text m_rowNumberText;
	public Text m_visibleCountText;
	public Image m_background;
	public ITableClick tableClickController;

	private int m_rowNumber;
	public int rowNumber
	{
		get
		{
			return m_rowNumber;
		}
		set
		{
			m_rowNumber = value;
			setRowInfo (m_rowNumber, sessionInfo);
		}

	}

	private LIVESession m_sessionInfo;
	public LIVESession sessionInfo
	{
		get
		{
			return m_sessionInfo;
		}
		set
		{
			m_sessionInfo = value;
			setRowInfo (rowNumber, m_sessionInfo);
		}
	}


	private void setRowInfo(int row, LIVESession sessionInfo)
	{
		if (sessionInfo != null)
		{
			m_rowNumberText.text = this.sessionInfo.id.ToString ();
			m_visibleCountText.text = this.sessionInfo.name;
			m_background.color = GetColorForRow (row);
		}
	}

	/*public void SetRowNumber(int rowNumber) {
		m_rowNumberText.text = "Row " + rowNumber.ToString();
		m_background.color = GetColorForRow(rowNumber);
	}*/

	private int m_numTimesBecameVisible;
	public void NotifyBecameVisible() {
		m_numTimesBecameVisible++;
		m_visibleCountText.text = "# rows this cell showed : " + m_numTimesBecameVisible.ToString();
	}

	private Color GetColorForRow(int row) {
		switch (row % 3) {
		case 0:
			return Color.blue;
		case 1:
			return Color.white;
		default:
			return Color.red;
		}
	}



	public void onClick(BaseEventData evt)
	{
		PointerEventData ptrEvt = evt as PointerEventData;
		if (ptrEvt != null)
		{
			SessionCell cell = ptrEvt.pointerPress.GetComponent<SessionCell> ();
			if ((cell != null) && (tableClickController != null))
				tableClickController.handleClick (ptrEvt, cell);
			
		}
	}

}

