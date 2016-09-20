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


	private Color GetColorForRow(int row) {
		switch (row % 2) {
		case 0:
			return new Color (150, 150, 150);
		case 1:
			return Color.white;
		default:
			return new Color (100, 100, 100);
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

