using UnityEngine;
using System.Collections;
using UnityEngine.EventSystems;

public class SessionViewController : MonoBehaviour, Tacticsoft.ITableViewDataSource, ITableClick {

	public SessionCell m_cellPrefab;
	public Tacticsoft.TableView m_tableView;
	public LIVESession[] sessionList;
	public Canvas canvas;
	public Planet_Data_Loader loader;

	private int m_numInstancesCreated = 0;

	// Use this for initialization
	void Start () {
		m_tableView.dataSource = this;
	}
	
	// Update is called once per frame
	void Update () {
		
	}

	/// <summary>
	/// Get the number of rows that a certain table should display
	/// </summary>
	public int GetNumberOfRowsForTableView(Tacticsoft.TableView tableView)
	{
		if (sessionList != null)
			return sessionList.Length;
		else
			return 0;
	}

	/// <summary>
	/// Get the height of a row of a certain cell in the table view
	/// </summary>
	public float GetHeightForRowInTableView(Tacticsoft.TableView tableView, int row)
	{
		return 50.0f;
	}


	/// <summary>
	/// Create a cell for a certain row in a table view.
	/// Callers should use tableView.GetReusableCell to cache objects
	/// </summary>
	public Tacticsoft.TableViewCell GetCellForRowInTableView(Tacticsoft.TableView tableView, int row)
	{
		SessionCell cell = tableView.GetReusableCell(m_cellPrefab.reuseIdentifier) as SessionCell;
		if (cell == null) {
			cell = (SessionCell)GameObject.Instantiate(m_cellPrefab);
			cell.name = "VisibleCounterCellInstance_" + (++m_numInstancesCreated).ToString();
		}
		cell.tableClickController = this;
		cell.rowNumber = row;
		cell.sessionInfo = sessionList [row];
		return cell;
	}

	public void handleClick(PointerEventData evt, SessionCell cell)
	{
		if ((evt.button == PointerEventData.InputButton.Left) && (evt.clickCount == 2))
		{
			print (cell.sessionInfo);
			canvas.gameObject.SetActive (false);
			loader.startPlayback ();
		}
	}
}
